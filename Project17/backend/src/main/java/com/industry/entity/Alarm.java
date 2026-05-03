package com.industry.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("alarm")
public class Alarm {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long deviceId;

    private String alarmType;

    private Integer alarmLevel;

    private String alarmMessage;

    private String currentValue;

    private String thresholdValue;

    private Integer status;

    private LocalDateTime handleTime;

    private String handleUser;

    private String handleRemark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
