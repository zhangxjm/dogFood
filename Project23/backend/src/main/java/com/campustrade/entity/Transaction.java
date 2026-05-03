package com.campustrade.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("transaction")
public class Transaction {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long productId;
    
    private Long sellerId;
    
    private Long buyerId;
    
    private BigDecimal price;
    
    private String status;
    
    private String remark;
    
    @TableField(exist = false)
    private Product product;
    
    @TableField(exist = false)
    private String sellerNickname;
    
    @TableField(exist = false)
    private String sellerAvatar;
    
    @TableField(exist = false)
    private String buyerNickname;
    
    @TableField(exist = false)
    private String buyerAvatar;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
