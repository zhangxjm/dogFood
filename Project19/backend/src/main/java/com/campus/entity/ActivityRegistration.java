package com.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("activity_registration")
public class ActivityRegistration {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long activityId;

    private Long userId;

    private Integer status;

    private LocalDateTime signTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String activityTitle;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private String realName;

    @TableField(exist = false)
    private String clubName;

    @TableField(exist = false)
    private String location;

    @TableField(exist = false)
    private LocalDateTime startTime;

    @TableField(exist = false)
    private LocalDateTime endTime;

    @TableField(exist = false)
    private Integer activityStatus;

    @TableField(exist = false)
    private Integer currentParticipants;

    @TableField(exist = false)
    private Integer maxParticipants;

    public static final int STATUS_CANCELLED = 0;
    public static final int STATUS_REGISTERED = 1;
    public static final int STATUS_COMPLETED = 2;
}
