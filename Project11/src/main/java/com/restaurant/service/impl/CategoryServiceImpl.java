package com.restaurant.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restaurant.entity.Category;
import com.restaurant.mapper.CategoryMapper;
import com.restaurant.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    
    @Override
    public Page<Category> pageList(Integer page, Integer pageSize, String name) {
        Page<Category> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(name)) {
            wrapper.like(Category::getName, name);
        }
        wrapper.orderByAsc(Category::getSort)
                .orderByDesc(Category::getCreateTime);
        
        return this.page(pageParam, wrapper);
    }
    
    @Override
    public List<Category> listEnabled() {
        return this.lambdaQuery()
                .eq(Category::getStatus, Category.STATUS_ENABLED)
                .orderByAsc(Category::getSort)
                .list();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveCategory(Category category) {
        category.setStatus(Category.STATUS_ENABLED);
        return this.save(category);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCategory(Category category) {
        return this.updateById(category);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer status) {
        Category category = new Category();
        category.setId(id);
        category.setStatus(status);
        return this.updateById(category);
    }
}
