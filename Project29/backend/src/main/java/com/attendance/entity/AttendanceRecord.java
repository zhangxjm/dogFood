package com.attendance.entity;

import com.attendance.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 考勤记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("attendance_record")
public class AttendanceRecord extends BaseEntity {
    private Long empId;
    private LocalDate recordDate;
    private LocalDateTime clockInTime;
    private LocalDateTime clockOutTime;
    private BigDecimal workHours;
    private Integer isLate;
    private Integer isEarly;
    private Integer isAbsent;
    private Integer status;
    private String remark;
}
