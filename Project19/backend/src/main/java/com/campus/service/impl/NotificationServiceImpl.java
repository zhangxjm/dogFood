package com.campus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.entity.ClubMember;
import com.campus.entity.Notification;
import com.campus.mapper.ClubMemberMapper;
import com.campus.mapper.NotificationMapper;
import com.campus.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    private final ClubMemberMapper clubMemberMapper;

    @Override
    public List<Notification> listByUserId(Long userId) {
        return baseMapper.listByUserId(userId);
    }

    @Override
    public Page<Notification> pageByUserId(Page<Notification> page, Long userId, Integer type, Boolean unreadOnly) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId);
        if (type != null) {
            wrapper.eq(Notification::getType, type);
        }
        if (Boolean.TRUE.equals(unreadOnly)) {
            wrapper.eq(Notification::getIsRead, Notification.READ_NO);
        }
        wrapper.orderByDesc(Notification::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public int countUnread(Long userId) {
        return baseMapper.countUnreadByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsRead(Long id, Long userId) {
        return baseMapper.markAsRead(id, userId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAllAsRead(Long userId) {
        return baseMapper.markAllAsRead(userId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendNotification(Long userId, Integer type, String title, String content,
                                   String relatedType, Long relatedId, Long senderId, Long clubId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setRelatedType(relatedType);
        notification.setRelatedId(relatedId);
        notification.setIsRead(Notification.READ_NO);
        notification.setSenderId(senderId);
        notification.setClubId(clubId);
        notification.setCreateTime(LocalDateTime.now());
        save(notification);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendNotificationToClub(Long clubId, Integer type, String title, String content,
                                         String relatedType, Long relatedId, Long senderId) {
        List<ClubMember> members = clubMemberMapper.selectList(
            new LambdaQueryWrapper<ClubMember>()
                .eq(ClubMember::getClubId, clubId)
                .eq(ClubMember::getStatus, ClubMember.STATUS_APPROVED)
        );

        if (members == null || members.isEmpty()) {
            return;
        }

        List<Long> userIds = members.stream()
            .map(ClubMember::getUserId)
            .filter(userId -> senderId == null || !userId.equals(senderId))
            .toList();

        sendNotificationToUsers(userIds, type, title, content, relatedType, relatedId, senderId, clubId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendNotificationToUsers(List<Long> userIds, Integer type, String title, String content,
                                          String relatedType, Long relatedId, Long senderId, Long clubId) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        for (Long userId : userIds) {
            sendNotification(userId, type, title, content, relatedType, relatedId, senderId, clubId);
        }
    }
}
