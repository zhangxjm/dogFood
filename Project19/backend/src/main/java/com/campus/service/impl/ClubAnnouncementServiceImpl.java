package com.campus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.common.ResultCode;
import com.campus.entity.Club;
import com.campus.entity.ClubAnnouncement;
import com.campus.entity.ClubMember;
import com.campus.entity.Notification;
import com.campus.mapper.ClubAnnouncementMapper;
import com.campus.mapper.ClubMapper;
import com.campus.mapper.ClubMemberMapper;
import com.campus.service.ClubAnnouncementService;
import com.campus.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClubAnnouncementServiceImpl extends ServiceImpl<ClubAnnouncementMapper, ClubAnnouncement> implements ClubAnnouncementService {

    private final ClubMapper clubMapper;
    private final ClubMemberMapper clubMemberMapper;
    private final NotificationService notificationService;

    @Override
    public ClubAnnouncement getByIdWithDetail(Long id) {
        return baseMapper.getByIdWithDetail(id);
    }

    @Override
    public List<ClubAnnouncement> listByClubId(Long clubId) {
        return baseMapper.listByClubId(clubId);
    }

    @Override
    public Page<ClubAnnouncement> pageByClubId(Page<ClubAnnouncement> page, Long clubId, Integer type) {
        LambdaQueryWrapper<ClubAnnouncement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClubAnnouncement::getClubId, clubId)
                .eq(ClubAnnouncement::getStatus, ClubAnnouncement.STATUS_NORMAL);
        if (type != null) {
            wrapper.eq(ClubAnnouncement::getType, type);
        }
        wrapper.orderByDesc(ClubAnnouncement::getIsTop)
                .orderByDesc(ClubAnnouncement::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createAnnouncement(ClubAnnouncement announcement, Long userId) {
        Club club = clubMapper.selectById(announcement.getClubId());
        if (club == null) {
            throw new RuntimeException("社团不存在");
        }

        ClubMember member = clubMemberMapper.selectOne(
            new LambdaQueryWrapper<ClubMember>()
                .eq(ClubMember::getClubId, announcement.getClubId())
                .eq(ClubMember::getUserId, userId)
        );

        if (member == null || member.getRole() != ClubMember.ROLE_PRESIDENT) {
            throw new RuntimeException(ResultCode.PERMISSION_DENIED.getMessage());
        }

        announcement.setPublisherId(userId);
        announcement.setReadCount(0);
        announcement.setStatus(ClubAnnouncement.STATUS_NORMAL);
        announcement.setCreateTime(LocalDateTime.now());
        announcement.setUpdateTime(LocalDateTime.now());

        if (announcement.getIsTop() == null) {
            announcement.setIsTop(ClubAnnouncement.TOP_NO);
        }
        if (announcement.getType() == null) {
            announcement.setType(ClubAnnouncement.TYPE_CLUB);
        }

        boolean saved = save(announcement);

        if (saved) {
            sendAnnouncementNotification(announcement, club);
        }

        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAnnouncement(ClubAnnouncement announcement, Long userId) {
        ClubAnnouncement existing = getById(announcement.getId());
        if (existing == null || existing.getStatus() == ClubAnnouncement.STATUS_DELETED) {
            throw new RuntimeException("公告不存在");
        }

        if (!existing.getPublisherId().equals(userId)) {
            ClubMember member = clubMemberMapper.selectOne(
                new LambdaQueryWrapper<ClubMember>()
                    .eq(ClubMember::getClubId, existing.getClubId())
                    .eq(ClubMember::getUserId, userId)
            );
            if (member == null || member.getRole() != ClubMember.ROLE_PRESIDENT) {
                throw new RuntimeException(ResultCode.PERMISSION_DENIED.getMessage());
            }
        }

        existing.setTitle(announcement.getTitle());
        existing.setContent(announcement.getContent());
        if (announcement.getType() != null) {
            existing.setType(announcement.getType());
        }
        if (announcement.getIsTop() != null) {
            existing.setIsTop(announcement.getIsTop());
        }
        existing.setUpdateTime(LocalDateTime.now());

        return updateById(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteAnnouncement(Long id, Long userId) {
        ClubAnnouncement existing = getById(id);
        if (existing == null || existing.getStatus() == ClubAnnouncement.STATUS_DELETED) {
            throw new RuntimeException("公告不存在");
        }

        if (!existing.getPublisherId().equals(userId)) {
            ClubMember member = clubMemberMapper.selectOne(
                new LambdaQueryWrapper<ClubMember>()
                    .eq(ClubMember::getClubId, existing.getClubId())
                    .eq(ClubMember::getUserId, userId)
            );
            if (member == null || member.getRole() != ClubMember.ROLE_PRESIDENT) {
                throw new RuntimeException(ResultCode.PERMISSION_DENIED.getMessage());
            }
        }

        existing.setStatus(ClubAnnouncement.STATUS_DELETED);
        existing.setUpdateTime(LocalDateTime.now());
        return updateById(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleTop(Long id, Long userId) {
        ClubAnnouncement existing = getById(id);
        if (existing == null || existing.getStatus() == ClubAnnouncement.STATUS_DELETED) {
            throw new RuntimeException("公告不存在");
        }

        ClubMember member = clubMemberMapper.selectOne(
            new LambdaQueryWrapper<ClubMember>()
                .eq(ClubMember::getClubId, existing.getClubId())
                .eq(ClubMember::getUserId, userId)
        );
        if (member == null || member.getRole() != ClubMember.ROLE_PRESIDENT) {
            throw new RuntimeException(ResultCode.PERMISSION_DENIED.getMessage());
        }

        existing.setIsTop(existing.getIsTop() == ClubAnnouncement.TOP_YES ? 
            ClubAnnouncement.TOP_NO : ClubAnnouncement.TOP_YES);
        existing.setUpdateTime(LocalDateTime.now());
        return updateById(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementReadCount(Long id) {
        ClubAnnouncement announcement = getById(id);
        if (announcement != null) {
            announcement.setReadCount(announcement.getReadCount() + 1);
            updateById(announcement);
        }
    }

    private void sendAnnouncementNotification(ClubAnnouncement announcement, Club club) {
        List<ClubMember> members = clubMemberMapper.selectList(
            new LambdaQueryWrapper<ClubMember>()
                .eq(ClubMember::getClubId, announcement.getClubId())
                .eq(ClubMember::getStatus, ClubMember.STATUS_APPROVED)
        );

        if (members == null || members.isEmpty()) {
            return;
        }

        String title;
        if (announcement.getType() == ClubAnnouncement.TYPE_ACTIVITY) {
            title = "【活动公告】" + announcement.getTitle();
        } else {
            title = "【社团通知】" + announcement.getTitle();
        }

        String content = StringUtils.hasText(announcement.getContent()) 
            ? announcement.getContent().substring(0, Math.min(announcement.getContent().length(), 100)) 
            : "";

        List<Long> userIds = members.stream()
            .map(ClubMember::getUserId)
            .filter(userId -> !userId.equals(announcement.getPublisherId()))
            .toList();

        if (!userIds.isEmpty()) {
            notificationService.sendNotificationToUsers(
                userIds,
                announcement.getType() == ClubAnnouncement.TYPE_ACTIVITY ? 
                    Notification.TYPE_ACTIVITY : Notification.TYPE_CLUB,
                title,
                content,
                Notification.RELATED_TYPE_CLUB,
                announcement.getClubId(),
                announcement.getPublisherId(),
                announcement.getClubId()
            );
        }
    }
}
