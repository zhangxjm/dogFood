package com.attendance.entity;

import com.attendance.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 加班申请实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("overtime_application")
public class OvertimeApplication extends BaseEntity {
    private Long empId;
    private Integer overtimeType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal overtimeHours;
    private String reason;
    private Integer status;
    private Long approverId;
    private LocalDateTime approveTime;
    private String approveRemark;
}
