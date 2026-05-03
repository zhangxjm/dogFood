package com.seckill.product.controller;

import com.seckill.common.annotation.RateLimiter;
import com.seckill.common.entity.Product;
import com.seckill.common.result.PageResult;
import com.seckill.common.result.Result;
import com.seckill.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "商品接口", description = "商品列表、详情、库存等接口")
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "获取商品列表")
    @RateLimiter(limit = 100, windowTime = 1)
    @GetMapping("/list")
    public Result<PageResult<Product>> getProductList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        PageResult<Product> result = productService.getProductList(pageNum, pageSize, status, keyword);
        return Result.success(result);
    }

    @Operation(summary = "获取商品详情")
    @RateLimiter(limit = 100, windowTime = 1)
    @GetMapping("/{id}")
    public Result<Product> getProductDetail(@PathVariable Long id) {
        Product product = productService.getProductDetail(id);
        return Result.success(product);
    }

    @Operation(summary = "获取商品库存")
    @RateLimiter(limit = 50, windowTime = 1)
    @GetMapping("/stock/{id}")
    public Result<Integer> getStock(@PathVariable Long id) {
        Product product = productService.getProductDetail(id);
        return Result.success(product.getStock());
    }
}
