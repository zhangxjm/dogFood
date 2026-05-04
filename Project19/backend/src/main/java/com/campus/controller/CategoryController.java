package com.campus.controller;

import com.campus.common.Result;
import com.campus.entity.ClubCategory;
import com.campus.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/list")
    public Result<List<ClubCategory>> getAllCategories() {
        List<ClubCategory> categories = categoryService.getAllCategories();
        return Result.success(categories);
    }

    @GetMapping("/enabled")
    public Result<List<ClubCategory>> getEnabledCategories() {
        List<ClubCategory> categories = categoryService.getEnabledCategories();
        return Result.success(categories);
    }

    @GetMapping("/{id}")
    public Result<ClubCategory> getCategoryById(@PathVariable Long id) {
        ClubCategory category = categoryService.getById(id);
        return Result.success(category);
    }

    @PostMapping("/create")
    public Result<Boolean> createCategory(@RequestBody ClubCategory category) {
        boolean success = categoryService.createCategory(category);
        return success ? Result.success("创建成功", true) : Result.failed("创建失败");
    }

    @PostMapping("/update")
    public Result<Boolean> updateCategory(@RequestBody ClubCategory category) {
        boolean success = categoryService.updateCategory(category);
        return success ? Result.success("更新成功", true) : Result.failed("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteCategory(@PathVariable Long id) {
        boolean success = categoryService.deleteCategory(id);
        return success ? Result.success("删除成功", true) : Result.failed("删除失败");
    }
}
