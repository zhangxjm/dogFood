package com.inventory.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inventory.common.PageResult;
import com.inventory.entity.Supplier;
import com.inventory.exception.BusinessException;
import com.inventory.mapper.SupplierMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SupplierService extends ServiceImpl<SupplierMapper, Supplier> {

    public PageResult<Supplier> pageQuery(Long current, Long size, String keyword) {
        Page<Supplier> page = new Page<>(current, size);
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Supplier::getSupplierName, keyword)
                    .or()
                    .like(Supplier::getContactPerson, keyword)
                    .or()
                    .like(Supplier::getPhone, keyword);
        }
        
        wrapper.orderByDesc(Supplier::getCreateTime);
        Page<Supplier> result = this.page(page, wrapper);
        
        return new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                result.getSize(),
                result.getCurrent()
        );
    }

    public List<Supplier> listAll() {
        return this.lambdaQuery()
                .eq(Supplier::getStatus, 1)
                .orderByDesc(Supplier::getCreateTime)
                .list();
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean createSupplier(Supplier supplier) {
        Supplier exist = this.lambdaQuery()
                .eq(Supplier::getSupplierName, supplier.getSupplierName())
                .one();
        
        if (exist != null) {
            throw new BusinessException("供应商名称已存在");
        }
        
        supplier.setStatus(1);
        return this.save(supplier);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateSupplier(Supplier supplier) {
        Supplier exist = this.getById(supplier.getId());
        if (exist == null) {
            throw new BusinessException("供应商不存在");
        }
        
        Supplier sameName = this.lambdaQuery()
                .eq(Supplier::getSupplierName, supplier.getSupplierName())
                .ne(Supplier::getId, supplier.getId())
                .one();
        
        if (sameName != null) {
            throw new BusinessException("供应商名称已存在");
        }
        
        return this.updateById(supplier);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSupplier(Long id) {
        return this.removeById(id);
    }
}
