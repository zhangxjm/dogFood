package com.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sale_order")
public class SaleOrder extends BaseEntity {

    private String orderNo;

    private String customerName;

    private String customerPhone;

    private Long warehouseId;

    private BigDecimal totalAmount;

    private Long operatorId;

    private String operatorName;

    private Integer status;

    private String remark;
}
