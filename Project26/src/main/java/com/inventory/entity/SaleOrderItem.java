package com.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sale_order_item")
public class SaleOrderItem extends BaseEntity {

    private Long orderId;

    private Long productId;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal amount;
}
