package com.estore.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_no", unique = true, nullable = false)
    private String orderNo;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "address_id", nullable = false)
    private Long addressId;
    
    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(name = "pay_amount", precision = 10, scale = 2)
    private BigDecimal payAmount;
    
    private Integer status;
    
    @Column(name = "pay_time")
    private LocalDateTime payTime;
    
    @Column(name = "delivery_time")
    private LocalDateTime deliveryTime;
    
    @Column(name = "confirm_time")
    private LocalDateTime confirmTime;
    
    @Column(name = "close_time")
    private LocalDateTime closeTime;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (status == null) {
            status = 0;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
