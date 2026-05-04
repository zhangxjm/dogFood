package com.attendance.entity;

import com.attendance.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 岗位实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_post")
public class SysPost extends BaseEntity {
    private String postName;
    private String postCode;
    private String postLevel;
    private String description;
    private Integer sort;
    private Integer status;
}
