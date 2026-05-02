package com.restaurant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.restaurant.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {
    
    Page<Category> pageList(Integer page, Integer pageSize, String name);
    
    List<Category> listEnabled();
    
    boolean saveCategory(Category category);
    
    boolean updateCategory(Category category);
    
    boolean updateStatus(Long id, Integer status);
}
