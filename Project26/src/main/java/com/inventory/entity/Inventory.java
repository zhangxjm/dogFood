package com.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("inventory")
public class Inventory extends BaseEntity {

    private Long productId;

    private Long warehouseId;

    private Integer quantity;

    private Integer frozenQuantity;

    private LocalDateTime lastPurchaseTime;

    private LocalDateTime lastSaleTime;
}
