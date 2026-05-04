package com.campus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.entity.ClubCategory;

import java.util.List;

public interface CategoryService extends IService<ClubCategory> {

    List<ClubCategory> getAllCategories();

    List<ClubCategory> getEnabledCategories();

    boolean createCategory(ClubCategory category);

    boolean updateCategory(ClubCategory category);

    boolean deleteCategory(Long id);
}
