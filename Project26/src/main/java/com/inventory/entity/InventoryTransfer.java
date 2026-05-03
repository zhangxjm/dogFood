package com.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("inventory_transfer")
public class InventoryTransfer extends BaseEntity {

    private String transferNo;

    private Long fromWarehouseId;

    private Long toWarehouseId;

    private Long operatorId;

    private String operatorName;

    private Integer status;

    private String remark;
}
