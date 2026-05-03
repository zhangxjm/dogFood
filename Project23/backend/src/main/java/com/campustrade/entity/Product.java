package com.campustrade.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long categoryId;
    
    private String title;
    
    private String description;
    
    private BigDecimal price;
    
    private BigDecimal originalPrice;
    
    private String images;
    
    private String coverImage;
    
    private String location;
    
    private String contact;
    
    private String condition;
    
    private String status;
    
    private Integer viewCount;
    
    private Integer favoriteCount;
    
    private Integer isTop;
    
    @TableField(exist = false)
    private String userNickname;
    
    @TableField(exist = false)
    private String categoryName;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
