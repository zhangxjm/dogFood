package com.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product")
public class Product extends BaseEntity {

    private String productCode;

    private String productName;

    private Long categoryId;

    private String unit;

    private String specification;

    private BigDecimal purchasePrice;

    private BigDecimal salePrice;

    private Integer minStock;

    private Integer maxStock;

    private String description;

    private Integer status;
}
