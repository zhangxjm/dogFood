package com.campustrade.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("message")
public class Message {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long productId;
    
    private Long fromUserId;
    
    private Long toUserId;
    
    private String content;
    
    private Integer isRead;
    
    @TableField(exist = false)
    private String senderUsername;
    
    @TableField(exist = false)
    private String senderNickname;
    
    @TableField(exist = false)
    private String senderAvatar;
    
    @TableField(exist = false)
    private String receiverUsername;
    
    @TableField(exist = false)
    private String receiverNickname;
    
    @TableField(exist = false)
    private String receiverAvatar;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableLogic
    private Integer deleted;
}
