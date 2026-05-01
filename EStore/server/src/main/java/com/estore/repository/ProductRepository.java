package com.estore.repository;

import com.estore.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByStatusAndIsDeleted(Integer status, Integer isDeleted, Pageable pageable);
    Page<Product> findByCategoryIdAndStatusAndIsDeleted(Long categoryId, Integer status, Integer isDeleted, Pageable pageable);
    List<Product> findTop10ByStatusAndIsDeletedOrderBySalesDesc(Integer status, Integer isDeleted);
}
