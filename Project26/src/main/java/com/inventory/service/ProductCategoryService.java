package com.inventory.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inventory.common.PageResult;
import com.inventory.entity.ProductCategory;
import com.inventory.exception.BusinessException;
import com.inventory.mapper.ProductCategoryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ProductCategoryService extends ServiceImpl<ProductCategoryMapper, ProductCategory> {

    public PageResult<ProductCategory> pageQuery(Long current, Long size, String keyword) {
        Page<ProductCategory> page = new Page<>(current, size);
        LambdaQueryWrapper<ProductCategory> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(ProductCategory::getCategoryName, keyword);
        }
        
        wrapper.orderByAsc(ProductCategory::getSort)
                .orderByDesc(ProductCategory::getCreateTime);
        
        Page<ProductCategory> result = this.page(page, wrapper);
        
        return new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                result.getSize(),
                result.getCurrent()
        );
    }

    public List<ProductCategory> listAll() {
        return this.lambdaQuery()
                .eq(ProductCategory::getStatus, 1)
                .orderByAsc(ProductCategory::getSort)
                .list();
    }

    public List<ProductCategory> listByParentId(Long parentId) {
        return this.lambdaQuery()
                .eq(ProductCategory::getParentId, parentId)
                .eq(ProductCategory::getStatus, 1)
                .orderByAsc(ProductCategory::getSort)
                .list();
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean createCategory(ProductCategory category) {
        ProductCategory exist = this.lambdaQuery()
                .eq(ProductCategory::getCategoryName, category.getCategoryName())
                .one();
        
        if (exist != null) {
            throw new BusinessException("分类名称已存在");
        }
        
        category.setStatus(1);
        return this.save(category);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateCategory(ProductCategory category) {
        ProductCategory exist = this.getById(category.getId());
        if (exist == null) {
            throw new BusinessException("分类不存在");
        }
        
        ProductCategory sameName = this.lambdaQuery()
                .eq(ProductCategory::getCategoryName, category.getCategoryName())
                .ne(ProductCategory::getId, category.getId())
                .one();
        
        if (sameName != null) {
            throw new BusinessException("分类名称已存在");
        }
        
        return this.updateById(category);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCategory(Long id) {
        List<ProductCategory> children = this.lambdaQuery()
                .eq(ProductCategory::getParentId, id)
                .list();
        
        if (!children.isEmpty()) {
            throw new BusinessException("存在子分类，不能删除");
        }
        
        return this.removeById(id);
    }
}
