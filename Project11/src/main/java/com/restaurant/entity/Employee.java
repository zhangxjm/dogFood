package com.restaurant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("employee")
public class Employee {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    
    private String password;
    
    private String name;
    
    private String phone;
    
    private String role;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
    
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_CASHIER = "CASHIER";
    public static final String ROLE_KITCHEN = "KITCHEN";
    
    public static final Integer STATUS_DISABLED = 0;
    public static final Integer STATUS_ENABLED = 1;
}
