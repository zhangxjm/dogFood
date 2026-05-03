package com.campustrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campustrade.entity.Product;
import com.campustrade.mapper.ProductMapper;
import com.campustrade.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private static final List<String> VALID_SORT_FIELDS = Arrays.asList("create_time", "price", "view_count");
    private static final List<String> VALID_ORDER = Arrays.asList("asc", "desc");

    @Override
    public Page<Product> getProductList(Integer pageNum, Integer pageSize, Long categoryId,
                                          String keyword, Double minPrice, Double maxPrice,
                                          String sortBy, String order) {
        Page<Product> page = new Page<>(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10);
        
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(Product::getStatus, "active");
        
        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Product::getTitle, keyword)
                    .or().like(Product::getDescription, keyword));
        }
        
        if (minPrice != null) {
            wrapper.ge(Product::getPrice, minPrice);
        }
        
        if (maxPrice != null) {
            wrapper.le(Product::getPrice, maxPrice);
        }
        
        String sortField = "create_time";
        if (sortBy != null) {
            switch (sortBy) {
                case "time":
                    sortField = "create_time";
                    break;
                case "price":
                    sortField = "price";
                    break;
                case "view":
                    sortField = "view_count";
                    break;
            }
        }
        
        boolean isAsc = "asc".equals(order);
        
        if ("create_time".equals(sortField)) {
            wrapper.orderBy(true, !isAsc, Product::getCreateTime);
        } else if ("price".equals(sortField)) {
            wrapper.orderBy(true, isAsc, Product::getPrice);
        } else if ("view_count".equals(sortField)) {
            wrapper.orderBy(true, !isAsc, Product::getViewCount);
        }
        
        return this.page(page, wrapper);
    }

    @Override
    public Page<Product> getMyProducts(Long userId, Integer pageNum, Integer pageSize, String status) {
        if (userId == null) {
            return new Page<>();
        }
        
        Page<Product> page = new Page<>(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10);
        
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getUserId, userId);
        
        if (StringUtils.hasText(status) && !"all".equals(status)) {
            wrapper.eq(Product::getStatus, status);
        }
        
        wrapper.orderByDesc(Product::getCreateTime);
        
        return this.page(page, wrapper);
    }

    @Override
    public Page<Product> getPendingProducts(Integer pageNum, Integer pageSize) {
        Page<Product> page = new Page<>(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10);
        
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, "pending")
                .orderByAsc(Product::getCreateTime);
        
        return this.page(page, wrapper);
    }

    @Override
    public boolean publishProduct(Product product) {
        if (product == null) {
            return false;
        }
        
        product.setStatus("pending");
        product.setViewCount(0);
        product.setFavoriteCount(0);
        product.setIsTop(0);
        
        return this.save(product);
    }

    @Override
    public boolean updateViewCount(Long productId) {
        if (productId == null) {
            return false;
        }
        
        Product product = this.getById(productId);
        if (product == null) {
            return false;
        }
        
        product.setViewCount(product.getViewCount() == null ? 1 : product.getViewCount() + 1);
        
        return this.updateById(product);
    }

    @Override
    public boolean auditProduct(Long productId, String status, String remark) {
        if (productId == null || !StringUtils.hasText(status)) {
            return false;
        }
        
        Product product = this.getById(productId);
        if (product == null) {
            return false;
        }
        
        product.setStatus(status);
        
        return this.updateById(product);
    }
}
