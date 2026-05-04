package com.attendance.entity;

import com.attendance.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
public class SysDept extends BaseEntity {
    private Long parentId;
    private String deptName;
    private String deptCode;
    private String leader;
    private String phone;
    private String email;
    private Integer sort;
    private Integer status;
}
