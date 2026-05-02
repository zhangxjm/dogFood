package com.estore.service;

import com.estore.common.PageResult;
import com.estore.entity.Product;
import com.estore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    
    public PageResult<Product> getList(Integer pageNum, Integer pageSize, Long categoryId) {
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Product> page;
        
        if (categoryId != null) {
            page = productRepository.findByCategoryIdAndStatusAndIsDeleted(categoryId, 1, 0, pageable);
        } else {
            page = productRepository.findByStatusAndIsDeleted(1, 0, pageable);
        }
        
        return PageResult.of(page.getContent(), page.getTotalElements(), pageNum, pageSize);
    }
    
    public PageResult<Product> listAll(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Product> page = productRepository.findAll(pageable);
        
        return PageResult.of(page.getContent(), page.getTotalElements(), pageNum, pageSize);
    }
    
    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
    }
    
    public List<Product> getHotProducts() {
        return productRepository.findTop10ByStatusAndIsDeletedOrderBySalesDesc(1, 0);
    }
    
    @Transactional
    public Product create(Product product) {
        if (product.getStock() == null) product.setStock(0);
        if (product.getSales() == null) product.setSales(0);
        if (product.getStatus() == null) product.setStatus(1);
        if (product.getIsDeleted() == null) product.setIsDeleted(0);
        
        return productRepository.save(product);
    }
    
    @Transactional
    public Product update(Long id, Product product) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        if (product.getCategoryId() != null) {
            existingProduct.setCategoryId(product.getCategoryId());
        }
        if (product.getName() != null) {
            existingProduct.setName(product.getName());
        }
        if (product.getDescription() != null) {
            existingProduct.setDescription(product.getDescription());
        }
        if (product.getMainImage() != null) {
            existingProduct.setMainImage(product.getMainImage());
        }
        if (product.getSubImages() != null) {
            existingProduct.setSubImages(product.getSubImages());
        }
        if (product.getOriginalPrice() != null) {
            existingProduct.setOriginalPrice(product.getOriginalPrice());
        }
        if (product.getPrice() != null) {
            existingProduct.setPrice(product.getPrice());
        }
        if (product.getStock() != null) {
            existingProduct.setStock(product.getStock());
        }
        if (product.getSales() != null) {
            existingProduct.setSales(product.getSales());
        }
        if (product.getStatus() != null) {
            existingProduct.setStatus(product.getStatus());
        }
        
        return productRepository.save(existingProduct);
    }
    
    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        product.setIsDeleted(1);
        productRepository.save(product);
    }
}
