package com.seckill.product.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seckill.common.entity.Product;
import com.seckill.common.result.PageResult;

public interface ProductService extends IService<Product> {

    PageResult<Product> getProductList(Integer pageNum, Integer pageSize, Integer status, String keyword);

    Product getProductDetail(Long productId);

    boolean deductStock(Long productId, Integer quantity);

    boolean addStock(Long productId, Integer quantity);
}
