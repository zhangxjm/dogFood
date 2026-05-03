package com.inventory.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inventory.common.PageResult;
import com.inventory.dto.InventoryTransferDTO;
import com.inventory.entity.InventoryTransfer;
import com.inventory.entity.InventoryTransferItem;
import com.inventory.exception.BusinessException;
import com.inventory.mapper.InventoryTransferItemMapper;
import com.inventory.mapper.InventoryTransferMapper;
import com.inventory.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryTransferService extends ServiceImpl<InventoryTransferMapper, InventoryTransfer> {

    private final InventoryTransferMapper transferMapper;
    private final InventoryTransferItemMapper transferItemMapper;
    private final InventoryService inventoryService;

    public PageResult<InventoryTransfer> pageQuery(Long current, Long size, String keyword, Integer status) {
        Page<InventoryTransfer> page = new Page<>(current, size);
        LambdaQueryWrapper<InventoryTransfer> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(InventoryTransfer::getTransferNo, keyword);
        }
        
        if (status != null) {
            wrapper.eq(InventoryTransfer::getStatus, status);
        }
        
        wrapper.orderByDesc(InventoryTransfer::getCreateTime);
        Page<InventoryTransfer> result = this.page(page, wrapper);
        
        return new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                result.getSize(),
                result.getCurrent()
        );
    }

    public InventoryTransfer getDetail(Long id) {
        InventoryTransfer transfer = this.getById(id);
        if (transfer == null) {
            throw new BusinessException("调拨单不存在");
        }
        return transfer;
    }

    public List<InventoryTransferItem> getItems(Long transferId) {
        return transferItemMapper.selectList(
                new LambdaQueryWrapper<InventoryTransferItem>()
                        .eq(InventoryTransferItem::getTransferId, transferId)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public InventoryTransfer createTransfer(InventoryTransferDTO dto, UserPrincipal user) {
        if (dto.getFromWarehouseId().equals(dto.getToWarehouseId())) {
            throw new BusinessException("调出仓库和调入仓库不能相同");
        }
        
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BusinessException("请添加调拨商品");
        }
        
        InventoryTransfer transfer = new InventoryTransfer();
        transfer.setTransferNo(generateTransferNo());
        transfer.setFromWarehouseId(dto.getFromWarehouseId());
        transfer.setToWarehouseId(dto.getToWarehouseId());
        transfer.setOperatorId(user.getUserId());
        transfer.setOperatorName(user.getUsername());
        transfer.setStatus(0);
        transfer.setRemark(dto.getRemark());
        
        this.save(transfer);
        
        for (InventoryTransferDTO.InventoryTransferItemDTO item : dto.getItems()) {
            InventoryTransferItem transferItem = new InventoryTransferItem();
            transferItem.setTransferId(transfer.getId());
            transferItem.setProductId(item.getProductId());
            transferItem.setQuantity(item.getQuantity());
            transferItemMapper.insert(transferItem);
        }
        
        return transfer;
    }

    @Transactional(rollbackFor = Exception.class)
    public void executeTransfer(Long transferId, UserPrincipal user) {
        InventoryTransfer transfer = this.getById(transferId);
        if (transfer == null) {
            throw new BusinessException("调拨单不存在");
        }
        
        if (transfer.getStatus() != 0) {
            throw new BusinessException("调拨单状态不允许执行");
        }
        
        List<InventoryTransferItem> items = getItems(transferId);
        if (items.isEmpty()) {
            throw new BusinessException("调拨单没有商品明细");
        }
        
        for (InventoryTransferItem item : items) {
            inventoryService.subtractStock(
                    item.getProductId(),
                    transfer.getFromWarehouseId(),
                    item.getQuantity(),
                    "TRANSFER_OUT",
                    transfer.getId(),
                    transfer.getTransferNo(),
                    user.getUserId(),
                    user.getUsername(),
                    "调拨出库"
            );
            
            inventoryService.addStock(
                    item.getProductId(),
                    transfer.getToWarehouseId(),
                    item.getQuantity(),
                    "TRANSFER_IN",
                    transfer.getId(),
                    transfer.getTransferNo(),
                    user.getUserId(),
                    user.getUsername(),
                    "调拨入库"
            );
        }
        
        transfer.setStatus(1);
        this.updateById(transfer);
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancelTransfer(Long transferId) {
        InventoryTransfer transfer = this.getById(transferId);
        if (transfer == null) {
            throw new BusinessException("调拨单不存在");
        }
        
        if (transfer.getStatus() != 0) {
            throw new BusinessException("调拨单状态不允许取消");
        }
        
        transfer.setStatus(2);
        this.updateById(transfer);
    }

    private String generateTransferNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%06d", (int) (Math.random() * 1000000));
        return "IT" + date + random;
    }
}
