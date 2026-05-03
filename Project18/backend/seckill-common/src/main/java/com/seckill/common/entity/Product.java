package com.seckill.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_product")
public class Product extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String productName;

    private String productTitle;

    private Long categoryId;

    private BigDecimal price;

    private Integer stock;

    private Integer stockWarning;

    private String unit;

    private String description;

    private String mainImage;

    private String subImages;

    private String detail;

    private Integer status;

    private Long sales;

    private Integer sort;
}
