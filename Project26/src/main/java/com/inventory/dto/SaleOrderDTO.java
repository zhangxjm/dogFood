package com.inventory.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SaleOrderDTO {

    private String customerName;
    private String customerPhone;
    private Long warehouseId;
    private String remark;
    private List<SaleOrderItemDTO> items;

    @Data
    public static class SaleOrderItemDTO {
        private Long productId;
        private Integer quantity;
        private BigDecimal unitPrice;
    }
}
