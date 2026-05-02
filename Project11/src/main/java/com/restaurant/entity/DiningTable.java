package com.restaurant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("dining_table")
public class DiningTable {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String tableNo;
    
    private String tableName;
    
    private Integer capacity;
    
    private Integer status;
    
    private String qrCode;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
    
    public static final Integer STATUS_FREE = 0;
    public static final Integer STATUS_OCCUPIED = 1;
    public static final Integer STATUS_RESERVED = 2;
}
