package com.estore.controller;

import com.estore.common.Result;
import com.estore.entity.Category;
import com.estore.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {
    
    private final CategoryService categoryService;
    
    @GetMapping("/categories")
    public Result<List<Category>> list() {
        try {
            List<Category> categories = categoryService.getAll();
            return Result.success(categories);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/admin/categories")
    public Result<List<Category>> listAll() {
        try {
            List<Category> categories = categoryService.listAll();
            return Result.success(categories);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/categories/{id}")
    public Result<Category> getById(@PathVariable Long id) {
        try {
            Category category = categoryService.getById(id);
            return Result.success(category);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/admin/categories")
    public Result<Category> create(@RequestBody Category category) {
        try {
            Category created = categoryService.create(category);
            return Result.success(created);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/admin/categories/{id}")
    public Result<Category> update(@PathVariable Long id, @RequestBody Category category) {
        try {
            Category updated = categoryService.update(id, category);
            return Result.success(updated);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/admin/categories/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            categoryService.delete(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
