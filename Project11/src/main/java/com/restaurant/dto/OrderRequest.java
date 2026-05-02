package com.restaurant.dto;

import com.restaurant.entity.OrderDetail;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRequest {
    
    private Long tableId;
    private String tableName;
    private Integer customerCount;
    private BigDecimal totalAmount;
    private String remark;
    private List<OrderDetail> details;
}
