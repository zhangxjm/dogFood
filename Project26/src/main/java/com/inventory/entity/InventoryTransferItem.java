package com.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("inventory_transfer_item")
public class InventoryTransferItem extends BaseEntity {

    private Long transferId;

    private Long productId;

    private Integer quantity;
}
