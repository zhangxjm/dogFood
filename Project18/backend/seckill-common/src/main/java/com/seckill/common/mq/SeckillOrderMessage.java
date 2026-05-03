package com.seckill.common.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillOrderMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long activityId;
    private Long productId;
    private Long userId;
    private Integer quantity;
    private BigDecimal seckillPrice;
    private String orderNo;
    private LocalDateTime createTime;
}
