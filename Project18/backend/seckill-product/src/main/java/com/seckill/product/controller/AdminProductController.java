package com.seckill.product.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.seckill.common.constant.RedisConstants;
import com.seckill.common.entity.Product;
import com.seckill.common.exception.BusinessException;
import com.seckill.common.result.PageResult;
import com.seckill.common.result.Result;
import com.seckill.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Tag(name = "商品管理接口", description = "管理后台商品增删改查接口")
@RestController
@RequestMapping("/admin/product")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Operation(summary = "获取商品分页列表")
    @GetMapping("/page")
    public Result<PageResult<Product>> getProductPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        PageResult<Product> result = productService.getProductList(pageNum, pageSize, status, keyword);
        return Result.success(result);
    }

    @Operation(summary = "新增商品")
    @PostMapping
    public Result<Long> addProduct(@RequestBody Product product) {
        productService.save(product);
        return Result.success(product.getId());
    }

    @Operation(summary = "更新商品")
    @PutMapping
    public Result<Boolean> updateProduct(@RequestBody Product product) {
        if (product.getId() == null) {
            throw BusinessException.of("商品ID不能为空");
        }
        
        boolean success = productService.updateById(product);
        
        String cacheKey = RedisConstants.PRODUCT_INFO_KEY + product.getId();
        redisTemplate.delete(cacheKey);
        
        return Result.success(success);
    }

    @Operation(summary = "删除商品")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteProduct(@PathVariable Long id) {
        productService.removeById(id);
        
        String cacheKey = RedisConstants.PRODUCT_INFO_KEY + id;
        redisTemplate.delete(cacheKey);
        
        return Result.success(true);
    }

    @Operation(summary = "更新商品状态")
    @PutMapping("/status/{id}")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        LambdaUpdateWrapper<Product> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Product::getId, id)
               .set(Product::getStatus, status);
        
        productService.update(wrapper);
        
        String cacheKey = RedisConstants.PRODUCT_INFO_KEY + id;
        redisTemplate.delete(cacheKey);
        
        return Result.success(true);
    }
}
