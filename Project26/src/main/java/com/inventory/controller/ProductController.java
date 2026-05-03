package com.inventory.controller;

import com.inventory.common.PageResult;
import com.inventory.common.Result;
import com.inventory.entity.Product;
import com.inventory.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public Result<PageResult<Product>> list(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {
        PageResult<Product> result = productService.pageQuery(current, size, keyword, categoryId);
        return Result.success(result);
    }

    @GetMapping("/all")
    public Result<List<Product>> listAll() {
        List<Product> result = productService.listAll();
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Product> getById(@PathVariable Long id) {
        Product product = productService.getById(id);
        return Result.success(product);
    }

    @PostMapping
    public Result<Product> create(@RequestBody Product product) {
        productService.createProduct(product);
        return Result.success("创建成功", product);
    }

    @PutMapping("/{id}")
    public Result<Product> update(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        productService.updateProduct(product);
        return Result.success("更新成功", product);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success();
    }
}
