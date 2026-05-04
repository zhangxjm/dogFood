package com.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("club_announcement")
public class ClubAnnouncement {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long clubId;

    private String title;

    private String content;

    private Integer type;

    private Integer isTop;

    private Long publisherId;

    private Integer readCount;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String clubName;

    @TableField(exist = false)
    private String publisherName;

    public static final int TYPE_CLUB = 1;
    public static final int TYPE_ACTIVITY = 2;

    public static final int TOP_NO = 0;
    public static final int TOP_YES = 1;

    public static final int STATUS_DELETED = 0;
    public static final int STATUS_NORMAL = 1;
}
