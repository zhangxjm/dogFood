package com.campus.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.entity.Notification;

import java.util.List;

public interface NotificationService extends IService<Notification> {

    List<Notification> listByUserId(Long userId);

    Page<Notification> pageByUserId(Page<Notification> page, Long userId, Integer type, Boolean unreadOnly);

    int countUnread(Long userId);

    boolean markAsRead(Long id, Long userId);

    boolean markAllAsRead(Long userId);

    void sendNotification(Long userId, Integer type, String title, String content, 
                           String relatedType, Long relatedId, Long senderId, Long clubId);

    void sendNotificationToClub(Long clubId, Integer type, String title, String content,
                                  String relatedType, Long relatedId, Long senderId);

    void sendNotificationToUsers(List<Long> userIds, Integer type, String title, String content,
                                   String relatedType, Long relatedId, Long senderId, Long clubId);
}
