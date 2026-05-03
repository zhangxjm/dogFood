package com.inventory.controller;

import com.inventory.common.PageResult;
import com.inventory.common.Result;
import com.inventory.entity.ProductCategory;
import com.inventory.service.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class ProductCategoryController {

    private final ProductCategoryService categoryService;

    @GetMapping
    public Result<PageResult<ProductCategory>> list(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword) {
        PageResult<ProductCategory> result = categoryService.pageQuery(current, size, keyword);
        return Result.success(result);
    }

    @GetMapping("/all")
    public Result<List<ProductCategory>> listAll() {
        List<ProductCategory> result = categoryService.listAll();
        return Result.success(result);
    }

    @GetMapping("/parent/{parentId}")
    public Result<List<ProductCategory>> listByParentId(@PathVariable Long parentId) {
        List<ProductCategory> result = categoryService.listByParentId(parentId);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<ProductCategory> getById(@PathVariable Long id) {
        ProductCategory category = categoryService.getById(id);
        return Result.success(category);
    }

    @PostMapping
    public Result<ProductCategory> create(@RequestBody ProductCategory category) {
        categoryService.createCategory(category);
        return Result.success("创建成功", category);
    }

    @PutMapping("/{id}")
    public Result<ProductCategory> update(@PathVariable Long id, @RequestBody ProductCategory category) {
        category.setId(id);
        categoryService.updateCategory(category);
        return Result.success("更新成功", category);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }
}
