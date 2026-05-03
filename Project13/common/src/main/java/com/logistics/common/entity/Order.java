package com.logistics.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_order")
public class Order {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String orderNo;

    private Long userId;

    private String senderName;

    private String senderPhone;

    private String senderAddress;

    private Double senderLat;

    private Double senderLng;

    private String receiverName;

    private String receiverPhone;

    private String receiverAddress;

    private Double receiverLat;

    private Double receiverLng;

    private String goodsName;

    private Double weight;

    private Double volume;

    private String remark;

    private Integer status;

    private BigDecimal freight;

    private LocalDateTime expectedTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
