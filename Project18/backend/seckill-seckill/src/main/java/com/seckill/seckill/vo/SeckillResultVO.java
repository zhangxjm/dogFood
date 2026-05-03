package com.seckill.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String status;
    private String message;
    private String orderNo;
    private Long activityId;
}
