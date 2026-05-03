package com.industry.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("device")
public class Device {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String deviceCode;

    private String deviceName;

    private String deviceType;

    private Integer status;

    private String location;

    private LocalDate installDate;

    private BigDecimal temperatureThreshold;

    private BigDecimal voltageMin;

    private BigDecimal voltageMax;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
