package com.restaurant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_detail")
public class OrderDetail {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long orderId;
    
    private Long dishId;
    
    private String dishName;
    
    private BigDecimal dishPrice;
    
    private Integer quantity;
    
    private BigDecimal totalPrice;
    
    private String remark;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
    
    public static final Integer STATUS_PENDING = 0;
    public static final Integer STATUS_COOKING = 1;
    public static final Integer STATUS_COMPLETED = 2;
    public static final Integer STATUS_REFUNDED = 3;
}
