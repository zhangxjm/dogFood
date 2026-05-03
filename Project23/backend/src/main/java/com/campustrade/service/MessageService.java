package com.campustrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campustrade.entity.Message;

import java.util.List;

public interface MessageService extends IService<Message> {
    boolean sendMessage(Long fromUserId, Long toUserId, Long productId, String content);
    List<Message> getReceivedMessages(Long userId);
    List<Message> getSentMessages(Long userId);
    List<Message> getChatHistory(Long userId, Long otherUserId, Long productId);
    boolean markAsRead(Long messageId);
    int getUnreadCount(Long userId);
}
