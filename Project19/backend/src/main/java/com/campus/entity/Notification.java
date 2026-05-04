package com.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("notification")
public class Notification {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Integer type;

    private String title;

    private String content;

    private String relatedType;

    private Long relatedId;

    private Integer isRead;

    private Long senderId;

    private Long clubId;

    private LocalDateTime createTime;

    private LocalDateTime readTime;

    @TableField(exist = false)
    private String senderName;

    @TableField(exist = false)
    private String clubName;

    public static final int TYPE_SYSTEM = 1;
    public static final int TYPE_CLUB = 2;
    public static final int TYPE_ACTIVITY = 3;
    public static final int TYPE_AUDIT = 4;

    public static final int READ_NO = 0;
    public static final int READ_YES = 1;

    public static final String RELATED_TYPE_CLUB = "club";
    public static final String RELATED_TYPE_ACTIVITY = "activity";
    public static final String RELATED_TYPE_CLUB_MEMBER = "club_member";
}
