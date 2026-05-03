package com.inventory.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PurchaseOrderDTO {

    private Long supplierId;
    private Long warehouseId;
    private String remark;
    private List<PurchaseOrderItemDTO> items;

    @Data
    public static class PurchaseOrderItemDTO {
        private Long productId;
        private Integer quantity;
        private BigDecimal unitPrice;
    }
}
