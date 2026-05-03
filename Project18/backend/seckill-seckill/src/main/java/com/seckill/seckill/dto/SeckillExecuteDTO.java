package com.seckill.seckill.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class SeckillExecuteDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "秒杀活动ID不能为空")
    private Long activityId;

    @Min(value = 1, message = "购买数量至少为1")
    private Integer quantity = 1;

    private String address;
    private String consignee;
    private String phone;
}
