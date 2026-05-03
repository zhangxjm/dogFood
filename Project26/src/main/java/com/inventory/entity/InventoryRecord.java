package com.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("inventory_record")
public class InventoryRecord extends BaseEntity {

    private String recordNo;

    private Long productId;

    private Long warehouseId;

    private String type;

    private Long referenceId;

    private String referenceNo;

    private Integer quantity;

    private Integer beforeQuantity;

    private Integer afterQuantity;

    private Long operatorId;

    private String operatorName;

    private String remark;
}
