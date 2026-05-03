package com.inventory.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inventory.common.PageResult;
import com.inventory.entity.Inventory;
import com.inventory.entity.InventoryRecord;
import com.inventory.entity.Product;
import com.inventory.entity.Warehouse;
import com.inventory.exception.BusinessException;
import com.inventory.mapper.InventoryMapper;
import com.inventory.mapper.InventoryRecordMapper;
import com.inventory.mapper.ProductMapper;
import com.inventory.mapper.WarehouseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService extends ServiceImpl<InventoryMapper, Inventory> {

    private final InventoryMapper inventoryMapper;
    private final InventoryRecordMapper inventoryRecordMapper;
    private final ProductMapper productMapper;
    private final WarehouseMapper warehouseMapper;

    public PageResult<Inventory> pageQuery(Long current, Long size, String keyword, Long warehouseId, Long categoryId) {
        Page<Inventory> page = new Page<>(current, size);
        
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        
        if (warehouseId != null && warehouseId > 0) {
            wrapper.eq(Inventory::getWarehouseId, warehouseId);
        }
        
        if (StringUtils.hasText(keyword) || categoryId != null && categoryId > 0) {
            List<Long> productIds = productMapper.selectList(
                    new LambdaQueryWrapper<Product>()
                            .and(w -> {
                                if (StringUtils.hasText(keyword)) {
                                    w.like(Product::getProductName, keyword)
                                            .or()
                                            .like(Product::getProductCode, keyword);
                                }
                                if (categoryId != null && categoryId > 0) {
                                    w.eq(Product::getCategoryId, categoryId);
                                }
                            })
            ).stream().map(Product::getId).toList();
            
            if (!productIds.isEmpty()) {
                wrapper.in(Inventory::getProductId, productIds);
            } else {
                return new PageResult<>(List.of(), 0L, size, current);
            }
        }
        
        wrapper.orderByDesc(Inventory::getCreateTime);
        Page<Inventory> result = this.page(page, wrapper);
        
        return new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                result.getSize(),
                result.getCurrent()
        );
    }

    public Inventory getByProductAndWarehouse(Long productId, Long warehouseId) {
        return inventoryMapper.findByProductAndWarehouse(productId, warehouseId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void addStock(Long productId, Long warehouseId, Integer quantity, 
                         String type, Long referenceId, String referenceNo,
                         Long operatorId, String operatorName, String remark) {
        
        Inventory inventory = inventoryMapper.findByProductAndWarehouse(productId, warehouseId);
        int beforeQuantity = 0;
        
        if (inventory == null) {
            inventory = new Inventory();
            inventory.setProductId(productId);
            inventory.setWarehouseId(warehouseId);
            inventory.setQuantity(quantity);
            inventory.setFrozenQuantity(0);
            this.save(inventory);
        } else {
            beforeQuantity = inventory.getQuantity();
            inventoryMapper.addQuantity(productId, warehouseId, quantity);
            inventory.setQuantity(beforeQuantity + quantity);
        }
        
        if ("PURCHASE".equals(type)) {
            inventory.setLastPurchaseTime(LocalDateTime.now());
            this.updateById(inventory);
        }
        
        createInventoryRecord(productId, warehouseId, type, referenceId, referenceNo,
                quantity, beforeQuantity, beforeQuantity + quantity, 
                operatorId, operatorName, remark);
    }

    @Transactional(rollbackFor = Exception.class)
    public void subtractStock(Long productId, Long warehouseId, Integer quantity,
                               String type, Long referenceId, String referenceNo,
                               Long operatorId, String operatorName, String remark) {
        
        Inventory inventory = inventoryMapper.findByProductAndWarehouse(productId, warehouseId);
        
        if (inventory == null) {
            throw new BusinessException("商品库存不存在");
        }
        
        if (inventory.getQuantity() < quantity) {
            Product product = productMapper.selectById(productId);
            Warehouse warehouse = warehouseMapper.selectById(warehouseId);
            throw new BusinessException(
                    String.format("商品[%s]在仓库[%s]的库存不足，当前库存：%d，需要：%d",
                            product != null ? product.getProductName() : productId,
                            warehouse != null ? warehouse.getWarehouseName() : warehouseId,
                            inventory.getQuantity(), quantity)
            );
        }
        
        int beforeQuantity = inventory.getQuantity();
        int afterQuantity = beforeQuantity - quantity;
        
        int updated = inventoryMapper.subtractQuantity(productId, warehouseId, quantity);
        if (updated == 0) {
            throw new BusinessException("扣减库存失败，请重试");
        }
        
        if ("SALE".equals(type)) {
            inventory.setLastSaleTime(LocalDateTime.now());
            inventory.setQuantity(afterQuantity);
            this.updateById(inventory);
        }
        
        createInventoryRecord(productId, warehouseId, type, referenceId, referenceNo,
                quantity, beforeQuantity, afterQuantity,
                operatorId, operatorName, remark);
    }

    private void createInventoryRecord(Long productId, Long warehouseId, String type,
                                        Long referenceId, String referenceNo,
                                        Integer quantity, int beforeQuantity, int afterQuantity,
                                        Long operatorId, String operatorName, String remark) {
        
        InventoryRecord record = new InventoryRecord();
        record.setRecordNo(generateRecordNo());
        record.setProductId(productId);
        record.setWarehouseId(warehouseId);
        record.setType(type);
        record.setReferenceId(referenceId);
        record.setReferenceNo(referenceNo);
        record.setQuantity(quantity);
        record.setBeforeQuantity(beforeQuantity);
        record.setAfterQuantity(afterQuantity);
        record.setOperatorId(operatorId);
        record.setOperatorName(operatorName);
        record.setRemark(remark);
        
        inventoryRecordMapper.insert(record);
    }

    private String generateRecordNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%06d", (int) (Math.random() * 1000000));
        return "IR" + date + random;
    }

    public List<Inventory> getWarningInventory() {
        List<Inventory> inventories = this.list();
        return inventories.stream()
                .filter(inv -> {
                    Product product = productMapper.selectById(inv.getProductId());
                    if (product == null) return false;
                    return inv.getQuantity() < product.getMinStock() ||
                            (product.getMaxStock() > 0 && inv.getQuantity() > product.getMaxStock());
                })
                .toList();
    }
}
