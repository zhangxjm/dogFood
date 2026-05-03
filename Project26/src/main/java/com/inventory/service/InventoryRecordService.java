package com.inventory.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inventory.common.PageResult;
import com.inventory.entity.InventoryRecord;
import com.inventory.mapper.InventoryRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class InventoryRecordService extends ServiceImpl<InventoryRecordMapper, InventoryRecord> {

    public PageResult<InventoryRecord> pageQuery(Long current, Long size, 
                                                   String keyword, Long productId, 
                                                   Long warehouseId, String type,
                                                   LocalDateTime startTime, LocalDateTime endTime) {
        Page<InventoryRecord> page = new Page<>(current, size);
        LambdaQueryWrapper<InventoryRecord> wrapper = new LambdaQueryWrapper<>();
        
        if (productId != null && productId > 0) {
            wrapper.eq(InventoryRecord::getProductId, productId);
        }
        
        if (warehouseId != null && warehouseId > 0) {
            wrapper.eq(InventoryRecord::getWarehouseId, warehouseId);
        }
        
        if (StringUtils.hasText(type)) {
            wrapper.eq(InventoryRecord::getType, type);
        }
        
        if (startTime != null) {
            wrapper.ge(InventoryRecord::getCreateTime, startTime);
        }
        
        if (endTime != null) {
            wrapper.le(InventoryRecord::getCreateTime, endTime);
        }
        
        wrapper.orderByDesc(InventoryRecord::getCreateTime);
        Page<InventoryRecord> result = this.page(page, wrapper);
        
        return new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                result.getSize(),
                result.getCurrent()
        );
    }
}
