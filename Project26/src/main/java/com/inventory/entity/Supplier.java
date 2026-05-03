package com.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("supplier")
public class Supplier extends BaseEntity {

    private String supplierName;

    private String contactPerson;

    private String phone;

    private String email;

    private String address;

    private String description;

    private Integer status;
}
