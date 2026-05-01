package com.estore.controller;

import com.estore.common.PageResult;
import com.estore.common.Result;
import com.estore.entity.Product;
import com.estore.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    
    @GetMapping("/products")
    public Result<PageResult<Product>> list(
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Long categoryId) {
        try {
            PageResult<Product> result = productService.getList(pageNum, pageSize, categoryId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/products/hot")
    public Result<List<Product>> hot() {
        try {
            List<Product> products = productService.getHotProducts();
            return Result.success(products);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/products/{id}")
    public Result<Product> getById(@PathVariable Long id) {
        try {
            Product product = productService.getById(id);
            return Result.success(product);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/admin/products")
    public Result<PageResult<Product>> listAll(
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize) {
        try {
            PageResult<Product> result = productService.listAll(pageNum, pageSize);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/admin/products")
    public Result<Product> create(@RequestBody Product product) {
        try {
            Product created = productService.create(product);
            return Result.success(created);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/admin/products/{id}")
    public Result<Product> update(@PathVariable Long id, @RequestBody Product product) {
        try {
            Product updated = productService.update(id, product);
            return Result.success(updated);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/admin/products/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            productService.delete(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
