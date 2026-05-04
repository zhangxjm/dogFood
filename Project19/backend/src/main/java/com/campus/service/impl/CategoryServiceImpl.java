package com.campus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.entity.Club;
import com.campus.entity.ClubCategory;
import com.campus.mapper.ClubCategoryMapper;
import com.campus.mapper.ClubMapper;
import com.campus.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<ClubCategoryMapper, ClubCategory> implements CategoryService {

    private final ClubMapper clubMapper;

    @Override
    public List<ClubCategory> getAllCategories() {
        return list(new LambdaQueryWrapper<ClubCategory>().orderByAsc(ClubCategory::getSort));
    }

    @Override
    public List<ClubCategory> getEnabledCategories() {
        return list(new LambdaQueryWrapper<ClubCategory>()
            .eq(ClubCategory::getStatus, ClubCategory.STATUS_ENABLED)
            .orderByAsc(ClubCategory::getSort));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createCategory(ClubCategory category) {
        category.setStatus(ClubCategory.STATUS_ENABLED);
        return save(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCategory(ClubCategory category) {
        return updateById(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCategory(Long id) {
        Long count = clubMapper.selectCount(
            new LambdaQueryWrapper<Club>().eq(Club::getCategoryId, id)
        );
        if (count > 0) {
            throw new RuntimeException("该分类下存在社团，无法删除");
        }
        return removeById(id);
    }
}
