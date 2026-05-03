package com.seckill.common.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderTimeoutMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderNo;
    private Long userId;
    private Long productId;
    private Long activityId;
    private Integer quantity;
    private LocalDateTime expireTime;
}
