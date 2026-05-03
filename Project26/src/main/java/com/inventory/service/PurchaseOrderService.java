package com.inventory.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inventory.common.PageResult;
import com.inventory.dto.PurchaseOrderDTO;
import com.inventory.entity.PurchaseOrder;
import com.inventory.entity.PurchaseOrderItem;
import com.inventory.exception.BusinessException;
import com.inventory.mapper.PurchaseOrderItemMapper;
import com.inventory.mapper.PurchaseOrderMapper;
import com.inventory.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService extends ServiceImpl<PurchaseOrderMapper, PurchaseOrder> {

    private final PurchaseOrderMapper purchaseOrderMapper;
    private final PurchaseOrderItemMapper purchaseOrderItemMapper;
    private final InventoryService inventoryService;

    public PageResult<PurchaseOrder> pageQuery(Long current, Long size, String keyword, Integer status) {
        Page<PurchaseOrder> page = new Page<>(current, size);
        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(PurchaseOrder::getOrderNo, keyword);
        }
        
        if (status != null) {
            wrapper.eq(PurchaseOrder::getStatus, status);
        }
        
        wrapper.orderByDesc(PurchaseOrder::getCreateTime);
        Page<PurchaseOrder> result = this.page(page, wrapper);
        
        return new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                result.getSize(),
                result.getCurrent()
        );
    }

    public PurchaseOrder getDetail(Long id) {
        PurchaseOrder order = this.getById(id);
        if (order == null) {
            throw new BusinessException("采购订单不存在");
        }
        return order;
    }

    public List<PurchaseOrderItem> getItems(Long orderId) {
        return purchaseOrderItemMapper.selectList(
                new LambdaQueryWrapper<PurchaseOrderItem>()
                        .eq(PurchaseOrderItem::getOrderId, orderId)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public PurchaseOrder createOrder(PurchaseOrderDTO dto, UserPrincipal user) {
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BusinessException("请添加采购商品");
        }
        
        PurchaseOrder order = new PurchaseOrder();
        order.setOrderNo(generateOrderNo());
        order.setSupplierId(dto.getSupplierId());
        order.setWarehouseId(dto.getWarehouseId());
        order.setOperatorId(user.getUserId());
        order.setOperatorName(user.getUsername());
        order.setStatus(0);
        order.setRemark(dto.getRemark());
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (PurchaseOrderDTO.PurchaseOrderItemDTO item : dto.getItems()) {
            BigDecimal amount = item.getUnitPrice().multiply(new BigDecimal(item.getQuantity()));
            totalAmount = totalAmount.add(amount);
        }
        order.setTotalAmount(totalAmount);
        
        this.save(order);
        
        for (PurchaseOrderDTO.PurchaseOrderItemDTO item : dto.getItems()) {
            PurchaseOrderItem orderItem = new PurchaseOrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(item.getProductId());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setUnitPrice(item.getUnitPrice());
            orderItem.setAmount(item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())));
            purchaseOrderItemMapper.insert(orderItem);
        }
        
        return order;
    }

    @Transactional(rollbackFor = Exception.class)
    public void inStock(Long orderId, UserPrincipal user) {
        PurchaseOrder order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("采购订单不存在");
        }
        
        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态不允许入库");
        }
        
        List<PurchaseOrderItem> items = getItems(orderId);
        if (items.isEmpty()) {
            throw new BusinessException("订单没有商品明细");
        }
        
        for (PurchaseOrderItem item : items) {
            inventoryService.addStock(
                    item.getProductId(),
                    order.getWarehouseId(),
                    item.getQuantity(),
                    "PURCHASE",
                    order.getId(),
                    order.getOrderNo(),
                    user.getUserId(),
                    user.getUsername(),
                    "采购入库"
            );
        }
        
        order.setStatus(1);
        this.updateById(order);
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderId) {
        PurchaseOrder order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("采购订单不存在");
        }
        
        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态不允许取消");
        }
        
        order.setStatus(2);
        this.updateById(order);
    }

    private String generateOrderNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%06d", (int) (Math.random() * 1000000));
        return "PO" + date + random;
    }
}
