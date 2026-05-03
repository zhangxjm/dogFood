package com.seckill.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_seckill_activity")
public class SeckillActivity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String activityName;

    private Long productId;

    private BigDecimal seckillPrice;

    private Integer seckillCount;

    private Integer seckillLimit;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer status;

    private Integer stock;

    private Integer sales;

    private String description;
}
