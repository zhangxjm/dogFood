package com.inventory.dto;

import lombok.Data;

import java.util.List;

@Data
public class InventoryTransferDTO {

    private Long fromWarehouseId;
    private Long toWarehouseId;
    private String remark;
    private List<InventoryTransferItemDTO> items;

    @Data
    public static class InventoryTransferItemDTO {
        private Long productId;
        private Integer quantity;
    }
}
