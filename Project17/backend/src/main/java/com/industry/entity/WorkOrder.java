package com.industry.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("work_order")
public class WorkOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long deviceId;

    private Long alarmId;

    private Integer orderType;

    private Integer priority;

    private String title;

    private String description;

    private Long assignTo;

    private Integer status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String resultRemark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
