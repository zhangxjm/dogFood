package com.attendance.entity;

import com.attendance.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 请假申请实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("leave_application")
public class LeaveApplication extends BaseEntity {
    private Long empId;
    private Integer leaveType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal leaveDays;
    private String reason;
    private Integer status;
    private Long approverId;
    private LocalDateTime approveTime;
    private String approveRemark;
}
