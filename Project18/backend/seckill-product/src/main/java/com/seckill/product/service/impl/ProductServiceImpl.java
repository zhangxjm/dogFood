package com.seckill.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seckill.common.constant.RedisConstants;
import com.seckill.common.entity.Product;
import com.seckill.common.exception.BusinessException;
import com.seckill.common.result.PageResult;
import com.seckill.product.mapper.ProductMapper;
import com.seckill.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public PageResult<Product> getProductList(Integer pageNum, Integer pageSize, Integer status, String keyword) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Product::getStatus, status);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Product::getProductName, keyword)
                   .or()
                   .like(Product::getProductTitle, keyword);
        }
        wrapper.orderByDesc(Product::getSort)
               .orderByDesc(Product::getCreateTime);
        
        page = page(page, wrapper);
        
        return PageResult.of(page.getRecords(), page.getTotal(), pageNum, pageSize);
    }

    @Override
    public Product getProductDetail(Long productId) {
        String cacheKey = RedisConstants.PRODUCT_INFO_KEY + productId;
        
        Product product = (Product) redisTemplate.opsForValue().get(cacheKey);
        if (product != null) {
            return product;
        }
        
        product = getById(productId);
        if (product == null) {
            throw BusinessException.of("商品不存在");
        }
        
        redisTemplate.opsForValue().set(cacheKey, product, RedisConstants.PRODUCT_CACHE_EXPIRE, TimeUnit.SECONDS);
        
        return product;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductStock(Long productId, Integer quantity) {
        int rows = baseMapper.deductStock(productId, quantity);
        if (rows <= 0) {
            log.warn("库存扣减失败: productId={}, quantity={}", productId, quantity);
            return false;
        }
        
        String cacheKey = RedisConstants.PRODUCT_INFO_KEY + productId;
        redisTemplate.delete(cacheKey);
        
        log.info("库存扣减成功: productId={}, quantity={}", productId, quantity);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addStock(Long productId, Integer quantity) {
        int rows = baseMapper.addStock(productId, quantity);
        if (rows <= 0) {
            log.warn("库存增加失败: productId={}, quantity={}", productId, quantity);
            return false;
        }
        
        String cacheKey = RedisConstants.PRODUCT_INFO_KEY + productId;
        redisTemplate.delete(cacheKey);
        
        log.info("库存增加成功: productId={}, quantity={}", productId, quantity);
        return true;
    }
}
