package com.industry.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("maintenance_log")
public class MaintenanceLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long deviceId;

    private Long workOrderId;

    private Long operatorId;

    private String maintenanceType;

    private String content;

    private String result;

    private Integer costTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
