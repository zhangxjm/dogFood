package com.industry.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("device_data")
public class DeviceData {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long deviceId;

    private BigDecimal temperature;

    private BigDecimal voltage;

    private BigDecimal current;

    private BigDecimal power;

    private Integer runtime;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
