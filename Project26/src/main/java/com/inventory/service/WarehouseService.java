package com.inventory.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inventory.common.PageResult;
import com.inventory.entity.Warehouse;
import com.inventory.exception.BusinessException;
import com.inventory.mapper.WarehouseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class WarehouseService extends ServiceImpl<WarehouseMapper, Warehouse> {

    public PageResult<Warehouse> pageQuery(Long current, Long size, String keyword) {
        Page<Warehouse> page = new Page<>(current, size);
        LambdaQueryWrapper<Warehouse> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Warehouse::getWarehouseName, keyword)
                    .or()
                    .like(Warehouse::getWarehouseCode, keyword)
                    .or()
                    .like(Warehouse::getManager, keyword);
        }
        
        wrapper.orderByDesc(Warehouse::getCreateTime);
        Page<Warehouse> result = this.page(page, wrapper);
        
        return new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                result.getSize(),
                result.getCurrent()
        );
    }

    public List<Warehouse> listAll() {
        return this.lambdaQuery()
                .eq(Warehouse::getStatus, 1)
                .orderByDesc(Warehouse::getCreateTime)
                .list();
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean createWarehouse(Warehouse warehouse) {
        Warehouse exist = this.lambdaQuery()
                .eq(Warehouse::getWarehouseCode, warehouse.getWarehouseCode())
                .one();
        
        if (exist != null) {
            throw new BusinessException("仓库编码已存在");
        }
        
        warehouse.setStatus(1);
        return this.save(warehouse);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateWarehouse(Warehouse warehouse) {
        Warehouse exist = this.getById(warehouse.getId());
        if (exist == null) {
            throw new BusinessException("仓库不存在");
        }
        
        Warehouse sameCode = this.lambdaQuery()
                .eq(Warehouse::getWarehouseCode, warehouse.getWarehouseCode())
                .ne(Warehouse::getId, warehouse.getId())
                .one();
        
        if (sameCode != null) {
            throw new BusinessException("仓库编码已存在");
        }
        
        return this.updateById(warehouse);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteWarehouse(Long id) {
        return this.removeById(id);
    }
}
