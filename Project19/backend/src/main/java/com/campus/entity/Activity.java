package com.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("activity")
public class Activity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long clubId;

    private String title;

    private String cover;

    private String description;

    private String location;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer maxParticipants;

    private Integer currentParticipants;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String clubName;

    public static final int STATUS_CANCELLED = 0;
    public static final int STATUS_REGISTRATION = 1;
    public static final int STATUS_ONGOING = 2;
    public static final int STATUS_FINISHED = 3;
}
