package com.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("purchase_order")
public class PurchaseOrder extends BaseEntity {

    private String orderNo;

    private Long supplierId;

    private Long warehouseId;

    private BigDecimal totalAmount;

    private Long operatorId;

    private String operatorName;

    private Integer status;

    private String remark;
}
