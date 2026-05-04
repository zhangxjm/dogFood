package com.homemaking.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("worker")
public class Worker {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long serviceCategoryId;
    
    private String idCard;
    
    private String idCardFront;
    
    private String idCardBack;
    
    private String skillDesc;
    
    private Integer workYears;
    
    private BigDecimal rating;
    
    private Integer orderCount;
    
    private String auditStatus;
    
    private String auditRemark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(exist = false)
    private String realName;
    
    @TableField(exist = false)
    private String phone;
    
    @TableField(exist = false)
    private String avatar;
    
    @TableField(exist = false)
    private String categoryName;
}
