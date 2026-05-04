package com.attendance.entity;

import com.attendance.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 员工档案实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("emp_employee")
public class EmpEmployee extends BaseEntity {
    private Long userId;
    private String empNo;
    private String empName;
    private Integer gender;
    private LocalDate birthday;
    private String idCard;
    private String phone;
    private String email;
    private String address;
    private Long deptId;
    private Long postId;
    private LocalDate entryDate;
    private LocalDate leaveDate;
    private Integer status;
}
