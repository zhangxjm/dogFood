package com.estore.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "category_id", nullable = false)
    private Long categoryId;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "main_image")
    private String mainImage;
    
    @Column(name = "sub_images", columnDefinition = "TEXT")
    private String subImages;
    
    @Column(name = "original_price", precision = 10, scale = 2)
    private BigDecimal originalPrice;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal price;
    
    private Integer stock;
    
    private Integer sales;
    
    private Integer status;
    
    @Column(name = "is_deleted")
    private Integer isDeleted;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (stock == null) {
            stock = 0;
        }
        if (sales == null) {
            sales = 0;
        }
        if (status == null) {
            status = 1;
        }
        if (isDeleted == null) {
            isDeleted = 0;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
