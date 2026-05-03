package com.jobrecruitment.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Message {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private Integer messageType;
    private String content;
    private Long relatedId;
    private Integer isRead;
    private LocalDateTime createTime;

    private String senderName;
    private String receiverName;

    public static final int TYPE_SYSTEM = 0;
    public static final int TYPE_CHAT = 1;
    public static final int TYPE_INTERVIEW = 2;
}
