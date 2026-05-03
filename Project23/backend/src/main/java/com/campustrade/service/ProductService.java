package com.campustrade.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campustrade.entity.Product;

public interface ProductService extends IService<Product> {
    Page<Product> getProductList(Integer pageNum, Integer pageSize, Long categoryId, 
                                  String keyword, Double minPrice, Double maxPrice,
                                  String sortBy, String order);
    
    Page<Product> getMyProducts(Long userId, Integer pageNum, Integer pageSize, String status);
    
    Page<Product> getPendingProducts(Integer pageNum, Integer pageSize);
    
    boolean publishProduct(Product product);
    
    boolean updateViewCount(Long productId);
    
    boolean auditProduct(Long productId, String status, String remark);
}
