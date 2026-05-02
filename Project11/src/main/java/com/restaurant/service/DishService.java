package com.restaurant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.restaurant.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    
    Page<Dish> pageList(Integer page, Integer pageSize, String name, Long categoryId, Integer status);
    
    List<Dish> listByCategory(Long categoryId);
    
    List<Dish> listEnabled();
    
    boolean saveDish(Dish dish);
    
    boolean updateDish(Dish dish);
    
    boolean updateStatus(Long id, Integer status);
    
    List<Dish> listByCategoryAndStatus(Long categoryId, Integer status);
}
