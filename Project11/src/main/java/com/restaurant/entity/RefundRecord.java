package com.restaurant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("refund_record")
public class RefundRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long orderId;
    
    private String orderNo;
    
    private BigDecimal refundAmount;
    
    private String refundReason;
    
    private String refundMethod;
    
    private Long operatorId;
    
    private String operatorName;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableLogic
    private Integer deleted;
}
