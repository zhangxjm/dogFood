package com.seckill.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_order")
public class Order extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String orderNo;

    private Long userId;

    private Long productId;

    private Long activityId;

    private String productName;

    private String productImage;

    private BigDecimal unitPrice;

    private Integer quantity;

    private BigDecimal totalAmount;

    private BigDecimal payAmount;

    private Integer status;

    private Integer paymentType;

    private LocalDateTime paymentTime;

    private String consignee;

    private String phone;

    private String address;

    private String remark;

    private LocalDateTime expireTime;

    private LocalDateTime cancelTime;

    private String cancelReason;
}
