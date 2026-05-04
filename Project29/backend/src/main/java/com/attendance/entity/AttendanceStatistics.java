package com.attendance.entity;

import com.attendance.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 考勤月统计实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("attendance_statistics")
public class AttendanceStatistics extends BaseEntity {
    private Long empId;
    private Integer statisticsYear;
    private Integer statisticsMonth;
    private Integer workDays;
    private Integer actualDays;
    private Integer lateDays;
    private Integer earlyDays;
    private Integer absentDays;
    private BigDecimal leaveDays;
    private BigDecimal overtimeHours;
    private Integer status;
}
