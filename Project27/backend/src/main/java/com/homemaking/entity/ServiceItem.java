package com.homemaking.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("service_item")
public class ServiceItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long categoryId;
    
    private String name;
    
    private String description;
    
    private BigDecimal price;
    
    private String unit;
    
    private String image;
    
    private Integer status;
    
    private Integer sort;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(exist = false)
    private String categoryName;
}
