package com.campustrade.controller;

import com.campustrade.common.Result;
import com.campustrade.entity.Category;
import com.campustrade.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/list")
    public Result<List<Category>> getCategoryList() {
        List<Category> categories = categoryService.getActiveCategories();
        return Result.success(categories);
    }

    @GetMapping("/all")
    public Result<List<Category>> getAllCategories(HttpServletRequest request) {
        String userRole = (String) request.getAttribute("userRole");
        if (!"admin".equals(userRole)) {
            return Result.error(403, "无权限访问");
        }
        
        List<Category> categories = categoryService.list();
        return Result.success(categories);
    }

    @PostMapping("/add")
    public Result<Category> addCategory(HttpServletRequest request, @RequestBody Category category) {
        String userRole = (String) request.getAttribute("userRole");
        if (!"admin".equals(userRole)) {
            return Result.error(403, "无权限访问");
        }
        
        if (!StringUtils.hasText(category.getName())) {
            return Result.error("分类名称不能为空");
        }
        
        category.setStatus(1);
        if (category.getSort() == null) {
            category.setSort(0);
        }
        
        boolean success = categoryService.save(category);
        if (success) {
            return Result.success("添加成功", category);
        }
        return Result.error("添加失败");
    }

    @PutMapping("/update/{id}")
    public Result<Category> updateCategory(HttpServletRequest request, 
                                            @PathVariable Long id, 
                                            @RequestBody Category category) {
        String userRole = (String) request.getAttribute("userRole");
        if (!"admin".equals(userRole)) {
            return Result.error(403, "无权限访问");
        }
        
        Category existingCategory = categoryService.getById(id);
        if (existingCategory == null) {
            return Result.error("分类不存在");
        }
        
        category.setId(id);
        boolean success = categoryService.updateById(category);
        
        if (success) {
            Category updated = categoryService.getById(id);
            return Result.success("修改成功", updated);
        }
        return Result.error("修改失败");
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteCategory(HttpServletRequest request, @PathVariable Long id) {
        String userRole = (String) request.getAttribute("userRole");
        if (!"admin".equals(userRole)) {
            return Result.error(403, "无权限访问");
        }
        
        boolean success = categoryService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }
}
