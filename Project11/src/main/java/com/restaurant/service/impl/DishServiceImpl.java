package com.restaurant.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restaurant.entity.Dish;
import com.restaurant.mapper.DishMapper;
import com.restaurant.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    
    @Override
    public Page<Dish> pageList(Integer page, Integer pageSize, String name, Long categoryId, Integer status) {
        Page<Dish> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(name)) {
            wrapper.like(Dish::getName, name);
        }
        if (categoryId != null) {
            wrapper.eq(Dish::getCategoryId, categoryId);
        }
        if (status != null) {
            wrapper.eq(Dish::getStatus, status);
        }
        wrapper.orderByAsc(Dish::getSort)
                .orderByDesc(Dish::getCreateTime);
        
        return this.page(pageParam, wrapper);
    }
    
    @Override
    public List<Dish> listByCategory(Long categoryId) {
        return this.lambdaQuery()
                .eq(Dish::getCategoryId, categoryId)
                .orderByAsc(Dish::getSort)
                .list();
    }
    
    @Override
    public List<Dish> listEnabled() {
        return this.lambdaQuery()
                .eq(Dish::getStatus, Dish.STATUS_ON)
                .orderByAsc(Dish::getSort)
                .list();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveDish(Dish dish) {
        dish.setStatus(Dish.STATUS_ON);
        return this.save(dish);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDish(Dish dish) {
        return this.updateById(dish);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer status) {
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);
        return this.updateById(dish);
    }
    
    @Override
    public List<Dish> listByCategoryAndStatus(Long categoryId, Integer status) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        if (categoryId != null) {
            wrapper.eq(Dish::getCategoryId, categoryId);
        }
        if (status != null) {
            wrapper.eq(Dish::getStatus, status);
        }
        wrapper.orderByAsc(Dish::getSort);
        return this.list(wrapper);
    }
}
