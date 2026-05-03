package com.campustrade.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campustrade.common.Result;
import com.campustrade.entity.Product;
import com.campustrade.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/list")
    public Result<Page<Product>> getProductList(
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String order) {
        
        Page<Product> page = productService.getProductList(
                pageNum, pageSize, categoryId, keyword, minPrice, maxPrice, sortBy, order);
        
        return Result.success(page);
    }

    @GetMapping("/detail/{id}")
    public Result<Product> getProductDetail(@PathVariable Long id) {
        if (id == null) {
            return Result.error("商品ID不能为空");
        }
        
        productService.updateViewCount(id);
        
        Product product = productService.getById(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        
        return Result.success(product);
    }

    @PostMapping("/publish")
    public Result<Product> publishProduct(HttpServletRequest request, @RequestBody Product product) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        
        if (!StringUtils.hasText(product.getTitle())) {
            return Result.error("商品标题不能为空");
        }
        if (product.getCategoryId() == null) {
            return Result.error("请选择商品分类");
        }
        if (product.getPrice() == null) {
            return Result.error("商品价格不能为空");
        }
        
        product.setUserId(userId);
        boolean success = productService.publishProduct(product);
        
        if (success) {
            return Result.success("发布成功，请等待审核", product);
        }
        return Result.error("发布失败");
    }

    @PutMapping("/update/{id}")
    public Result<Product> updateProduct(HttpServletRequest request, 
                                          @PathVariable Long id, 
                                          @RequestBody Product product) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        
        Product existingProduct = productService.getById(id);
        if (existingProduct == null) {
            return Result.error("商品不存在");
        }
        if (!existingProduct.getUserId().equals(userId)) {
            return Result.error("无权修改此商品");
        }
        
        product.setId(id);
        product.setUserId(null);
        product.setStatus(null);
        product.setViewCount(null);
        product.setFavoriteCount(null);
        
        boolean success = productService.updateById(product);
        if (success) {
            Product updated = productService.getById(id);
            return Result.success("修改成功", updated);
        }
        return Result.error("修改失败");
    }

    @PostMapping("/take-down/{id}")
    public Result<Void> takeDownProduct(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        
        Product product = productService.getById(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        if (!product.getUserId().equals(userId)) {
            return Result.error("无权操作此商品");
        }
        
        product.setStatus("removed");
        boolean success = productService.updateById(product);
        
        if (success) {
            return Result.success("下架成功");
        }
        return Result.error("下架失败");
    }

    @GetMapping("/my")
    public Result<Page<Product>> getMyProducts(
            HttpServletRequest request,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String status) {
        
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        
        Page<Product> page = productService.getMyProducts(userId, pageNum, pageSize, status);
        return Result.success(page);
    }

    @GetMapping("/check-favorite/{id}")
    public Result<Boolean> checkFavorite(HttpServletRequest request, @PathVariable Long id) {
        return Result.success(false);
    }
}
