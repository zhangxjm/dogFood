package com.logistics.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_logistics_track")
public class LogisticsTrack {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long logisticsId;

    private Long orderId;

    private String orderNo;

    private String location;

    private Double lat;

    private Double lng;

    private String description;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
