package com.campus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.common.ResultCode;
import com.campus.entity.Activity;
import com.campus.entity.ActivityRegistration;
import com.campus.entity.Club;
import com.campus.entity.ClubMember;
import com.campus.mapper.ActivityMapper;
import com.campus.mapper.ActivityRegistrationMapper;
import com.campus.mapper.ClubMapper;
import com.campus.mapper.ClubMemberMapper;
import com.campus.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {

    private final ActivityRegistrationMapper registrationMapper;
    private final ClubMapper clubMapper;
    private final ClubMemberMapper clubMemberMapper;

    @Override
    public Page<Activity> pageActivities(Page<Activity> page, String title, Long clubId, Integer status) {
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(title)) {
            wrapper.like(Activity::getTitle, title);
        }
        if (clubId != null) {
            wrapper.eq(Activity::getClubId, clubId);
        }
        if (status != null) {
            wrapper.eq(Activity::getStatus, status);
        }
        wrapper.orderByDesc(Activity::getCreateTime);
        Page<Activity> result = page(page, wrapper);
        
        result.getRecords().forEach(activity -> {
            Activity detail = baseMapper.getByIdWithDetail(activity.getId());
            if (detail != null) {
                activity.setClubName(detail.getClubName());
            }
        });
        
        return result;
    }

    @Override
    public Activity getActivityDetail(Long id) {
        return baseMapper.getByIdWithDetail(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createActivity(Activity activity, Long userId) {
        Club club = clubMapper.selectById(activity.getClubId());
        if (club == null) {
            throw new RuntimeException("社团不存在");
        }
        
        ClubMember member = clubMemberMapper.selectOne(
            new LambdaQueryWrapper<ClubMember>()
                .eq(ClubMember::getClubId, activity.getClubId())
                .eq(ClubMember::getUserId, userId)
        );
        
        if (member == null || member.getRole() != ClubMember.ROLE_PRESIDENT) {
            throw new RuntimeException("只有社长可以发布活动");
        }
        
        activity.setCurrentParticipants(0);
        activity.setStatus(Activity.STATUS_REGISTRATION);
        return save(activity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateActivity(Activity activity) {
        return updateById(activity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteActivity(Long id) {
        Long count = registrationMapper.selectCount(
            new LambdaQueryWrapper<ActivityRegistration>()
                .eq(ActivityRegistration::getActivityId, id)
                .ne(ActivityRegistration::getStatus, ActivityRegistration.STATUS_CANCELLED)
        );
        if (count > 0) {
            throw new RuntimeException("该活动已有用户报名，无法删除");
        }
        return removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean registerActivity(Long activityId, Long userId) {
        Activity activity = getById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        if (activity.getStatus() != Activity.STATUS_REGISTRATION) {
            throw new RuntimeException("活动不在报名期");
        }
        
        ActivityRegistration existing = registrationMapper.selectOne(
            new LambdaQueryWrapper<ActivityRegistration>()
                .eq(ActivityRegistration::getActivityId, activityId)
                .eq(ActivityRegistration::getUserId, userId)
        );
        
        if (existing != null && existing.getStatus() != ActivityRegistration.STATUS_CANCELLED) {
            throw new RuntimeException(ResultCode.ALREADY_REGISTERED.getMessage());
        }
        
        if (activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            throw new RuntimeException(ResultCode.ACTIVITY_FULL.getMessage());
        }
        
        int rows;
        if (existing != null) {
            existing.setStatus(ActivityRegistration.STATUS_REGISTERED);
            rows = registrationMapper.updateById(existing);
        } else {
            ActivityRegistration registration = new ActivityRegistration();
            registration.setActivityId(activityId);
            registration.setUserId(userId);
            registration.setStatus(ActivityRegistration.STATUS_REGISTERED);
            rows = registrationMapper.insert(registration);
        }
        
        if (rows > 0) {
            activity.setCurrentParticipants(activity.getCurrentParticipants() + 1);
            updateById(activity);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelRegistration(Long activityId, Long userId) {
        ActivityRegistration registration = registrationMapper.selectOne(
            new LambdaQueryWrapper<ActivityRegistration>()
                .eq(ActivityRegistration::getActivityId, activityId)
                .eq(ActivityRegistration::getUserId, userId)
        );
        
        if (registration == null || registration.getStatus() == ActivityRegistration.STATUS_CANCELLED) {
            throw new RuntimeException("未报名该活动");
        }
        
        registration.setStatus(ActivityRegistration.STATUS_CANCELLED);
        int rows = registrationMapper.updateById(registration);
        
        if (rows > 0) {
            Activity activity = getById(activityId);
            if (activity != null) {
                activity.setCurrentParticipants(Math.max(0, activity.getCurrentParticipants() - 1));
                updateById(activity);
            }
            return true;
        }
        return false;
    }

    @Override
    public List<ActivityRegistration> getActivityRegistrations(Long activityId) {
        return registrationMapper.listByActivityId(activityId);
    }

    @Override
    public List<ActivityRegistration> getMyActivities(Long userId) {
        List<ActivityRegistration> registrations = registrationMapper.listByUserId(userId);
        return registrations.stream()
            .filter(r -> r.getStatus() == ActivityRegistration.STATUS_REGISTERED)
            .collect(Collectors.toList());
    }

    @Override
    public List<Activity> getClubActivities(Long clubId) {
        return baseMapper.listByClubId(clubId);
    }
}
