package com.campus.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.entity.Activity;
import com.campus.entity.ActivityRegistration;

import java.util.List;

public interface ActivityService extends IService<Activity> {

    Page<Activity> pageActivities(Page<Activity> page, String title, Long clubId, Integer status);

    Activity getActivityDetail(Long id);

    boolean createActivity(Activity activity, Long userId);

    boolean updateActivity(Activity activity);

    boolean deleteActivity(Long id);

    boolean registerActivity(Long activityId, Long userId);

    boolean cancelRegistration(Long activityId, Long userId);

    List<ActivityRegistration> getActivityRegistrations(Long activityId);

    List<ActivityRegistration> getMyActivities(Long userId);

    List<Activity> getClubActivities(Long clubId);
}
