package com.inventory.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inventory.common.PageResult;
import com.inventory.entity.Product;
import com.inventory.exception.BusinessException;
import com.inventory.mapper.ProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ProductService extends ServiceImpl<ProductMapper, Product> {

    public PageResult<Product> pageQuery(Long current, Long size, String keyword, Long categoryId) {
        Page<Product> page = new Page<>(current, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Product::getProductName, keyword)
                    .or()
                    .like(Product::getProductCode, keyword);
        }
        
        if (categoryId != null && categoryId > 0) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        
        wrapper.orderByDesc(Product::getCreateTime);
        Page<Product> result = this.page(page, wrapper);
        
        return new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                result.getSize(),
                result.getCurrent()
        );
    }

    public List<Product> listAll() {
        return this.lambdaQuery()
                .eq(Product::getStatus, 1)
                .orderByDesc(Product::getCreateTime)
                .list();
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean createProduct(Product product) {
        Product exist = this.lambdaQuery()
                .eq(Product::getProductCode, product.getProductCode())
                .one();
        
        if (exist != null) {
            throw new BusinessException("商品编码已存在");
        }
        
        product.setStatus(1);
        return this.save(product);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateProduct(Product product) {
        Product exist = this.getById(product.getId());
        if (exist == null) {
            throw new BusinessException("商品不存在");
        }
        
        Product sameCode = this.lambdaQuery()
                .eq(Product::getProductCode, product.getProductCode())
                .ne(Product::getId, product.getId())
                .one();
        
        if (sameCode != null) {
            throw new BusinessException("商品编码已存在");
        }
        
        return this.updateById(product);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteProduct(Long id) {
        return this.removeById(id);
    }
}
