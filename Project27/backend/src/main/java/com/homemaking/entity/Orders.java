package com.homemaking.entity;

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
    
    private Long userId;
    
    private Long workerId;
    
    private Long serviceItemId;
    
    private String serviceName;
    
    private Integer serviceQuantity;
    
    private BigDecimal totalAmount;
    
    private String serviceAddress;
    
    private String contactName;
    
    private String contactPhone;
    
    private LocalDateTime serviceTime;
    
    private String status;
    
    private String rejectReason;
    
    private String remark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(exist = false)
    private String userName;
    
    @TableField(exist = false)
    private String userPhone;
    
    @TableField(exist = false)
    private String workerName;
    
    @TableField(exist = false)
    private String workerPhone;
}
