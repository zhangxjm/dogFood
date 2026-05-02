package com.restaurant.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.restaurant.common.Result;
import com.restaurant.entity.Category;
import com.restaurant.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    
    private final CategoryService categoryService;
    
    @GetMapping
    public String list() {
        return "category/list";
    }
    
    @GetMapping("/list")
    @ResponseBody
    public Result<Page<Category>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name) {
        
        Page<Category> result = categoryService.pageList(page, pageSize, name);
        return Result.success(result);
    }
    
    @GetMapping("/list-enabled")
    @ResponseBody
    public Result<List<Category>> listEnabled() {
        List<Category> list = categoryService.listEnabled();
        return Result.success(list);
    }
    
    @GetMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String add() {
        return "category/add";
    }
    
    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public Result<Boolean> save(@RequestBody Category category) {
        boolean result = categoryService.saveCategory(category);
        return result ? Result.success(true) : Result.error("保存失败");
    }
    
    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String edit(@PathVariable Long id, Model model) {
        Category category = categoryService.getById(id);
        model.addAttribute("category", category);
        return "category/edit";
    }
    
    @PostMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public Result<Boolean> update(@RequestBody Category category) {
        boolean result = categoryService.updateCategory(category);
        return result ? Result.success(true) : Result.error("更新失败");
    }
    
    @PostMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public Result<Boolean> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        boolean result = categoryService.updateStatus(id, status);
        return result ? Result.success(true) : Result.error("更新失败");
    }
}
