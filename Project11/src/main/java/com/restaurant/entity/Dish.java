package com.restaurant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("dish")
public class Dish {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long categoryId;
    
    private String name;
    
    private BigDecimal price;
    
    private String image;
    
    private String description;
    
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
    
    public static final Integer STATUS_OFF = 0;
    public static final Integer STATUS_ON = 1;
}
