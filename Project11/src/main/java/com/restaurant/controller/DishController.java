package com.restaurant.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.restaurant.common.Result;
import com.restaurant.entity.Category;
import com.restaurant.entity.Dish;
import com.restaurant.service.CategoryService;
import com.restaurant.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/dish")
@RequiredArgsConstructor
public class DishController {
    
    private final DishService dishService;
    private final CategoryService categoryService;
    
    @GetMapping
    public String list(Model model) {
        List<Category> categories = categoryService.listEnabled();
        model.addAttribute("categories", categories);
        return "dish/list";
    }
    
    @GetMapping("/list")
    @ResponseBody
    public Result<Page<Dish>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status) {
        
        Page<Dish> result = dishService.pageList(page, pageSize, name, categoryId, status);
        return Result.success(result);
    }
    
    @GetMapping("/list-enabled")
    @ResponseBody
    public Result<List<Dish>> listEnabled(
            @RequestParam(required = false) Long categoryId) {
        
        List<Dish> list;
        if (categoryId != null) {
            list = dishService.listByCategoryAndStatus(categoryId, Dish.STATUS_ON);
        } else {
            list = dishService.listEnabled();
        }
        return Result.success(list);
    }
    
    @GetMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String add(Model model) {
        List<Category> categories = categoryService.listEnabled();
        model.addAttribute("categories", categories);
        return "dish/add";
    }
    
    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public Result<Boolean> save(@RequestBody Dish dish) {
        boolean result = dishService.saveDish(dish);
        return result ? Result.success(true) : Result.error("保存失败");
    }
    
    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String edit(@PathVariable Long id, Model model) {
        Dish dish = dishService.getById(id);
        List<Category> categories = categoryService.listEnabled();
        model.addAttribute("dish", dish);
        model.addAttribute("categories", categories);
        return "dish/edit";
    }
    
    @PostMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public Result<Boolean> update(@RequestBody Dish dish) {
        boolean result = dishService.updateDish(dish);
        return result ? Result.success(true) : Result.error("更新失败");
    }
    
    @PostMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public Result<Boolean> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        boolean result = dishService.updateStatus(id, status);
        return result ? Result.success(true) : Result.error("更新失败");
    }
}
