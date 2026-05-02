package com.restaurant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Orders {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String orderNo;
    
    private Long tableId;
    
    private String tableName;
    
    private Integer customerCount;
    
    private BigDecimal totalAmount;
    
    private BigDecimal discountAmount;
    
    private BigDecimal payableAmount;
    
    private BigDecimal paidAmount;
    
    private String paymentMethod;
    
    private Integer status;
    
    private String remark;
    
    private Long cashierId;
    
    private String cashierName;
    
    private Integer kitchenStatus;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    private LocalDateTime paidTime;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(exist = false)
    private String statusText;
    
    @TableField(exist = false)
    private String kitchenStatusText;
    
    public static final Integer STATUS_PENDING_ORDER = 0;
    public static final Integer STATUS_PENDING_PAYMENT = 1;
    public static final Integer STATUS_PAID = 2;
    public static final Integer STATUS_COMPLETED = 3;
    public static final Integer STATUS_CANCELLED = 4;
    public static final Integer STATUS_REFUNDED = 5;
    
    public static final Integer KITCHEN_STATUS_PENDING = 0;
    public static final Integer KITCHEN_STATUS_CONFIRMED = 1;
    public static final Integer KITCHEN_STATUS_COOKING = 2;
    public static final Integer KITCHEN_STATUS_COMPLETED = 3;
    
    public static final String PAYMENT_CASH = "CASH";
    public static final String PAYMENT_WECHAT = "WECHAT";
    public static final String PAYMENT_ALIPAY = "ALIPAY";
    public static final String PAYMENT_CARD = "CARD";
}
