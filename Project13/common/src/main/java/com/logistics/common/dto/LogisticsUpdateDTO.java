package com.logistics.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LogisticsUpdateDTO {
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    private String orderNo;

    @NotBlank(message = "当前位置不能为空")
    private String location;

    private Double lat;
    private Double lng;

    @NotBlank(message = "状态描述不能为空")
    private String description;

    @NotNull(message = "状态不能为空")
    private Integer status;

    private String courierName;
    private String courierPhone;
}
