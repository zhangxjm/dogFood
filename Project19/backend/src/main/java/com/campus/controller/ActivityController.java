package com.campus.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.Result;
import com.campus.entity.Activity;
import com.campus.entity.ActivityRegistration;
import com.campus.service.ActivityService;
import com.campus.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;
    private final JwtUtil jwtUtil;

    @GetMapping("/list")
    public Result<Page<Activity>> listActivities(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long clubId,
            @RequestParam(required = false) Integer status) {
        
        Page<Activity> page = new Page<>(current, size);
        Page<Activity> result = activityService.pageActivities(page, title, clubId, status);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Activity> getActivityDetail(@PathVariable Long id) {
        Activity activity = activityService.getActivityDetail(id);
        return Result.success(activity);
    }

    @PostMapping("/create")
    public Result<Boolean> createActivity(
            @RequestBody Activity activity,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.failed("请先登录");
        }
        
        boolean success = activityService.createActivity(activity, userId);
        return success ? Result.success("创建成功", true) : Result.failed("创建失败");
    }

    @PostMapping("/update")
    public Result<Boolean> updateActivity(@RequestBody Activity activity) {
        boolean success = activityService.updateActivity(activity);
        return success ? Result.success("更新成功", true) : Result.failed("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteActivity(@PathVariable Long id) {
        boolean success = activityService.deleteActivity(id);
        return success ? Result.success("删除成功", true) : Result.failed("删除失败");
    }

    @PostMapping("/register/{activityId}")
    public Result<Boolean> registerActivity(
            @PathVariable Long activityId,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.failed("请先登录");
        }
        
        boolean success = activityService.registerActivity(activityId, userId);
        return success ? Result.success("报名成功", true) : Result.failed("报名失败");
    }

    @PostMapping("/cancel/{activityId}")
    public Result<Boolean> cancelRegistration(
            @PathVariable Long activityId,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.failed("请先登录");
        }
        
        boolean success = activityService.cancelRegistration(activityId, userId);
        return success ? Result.success("取消报名成功", true) : Result.failed("取消报名失败");
    }

    @GetMapping("/registrations/{activityId}")
    public Result<List<ActivityRegistration>> getActivityRegistrations(@PathVariable Long activityId) {
        List<ActivityRegistration> registrations = activityService.getActivityRegistrations(activityId);
        return Result.success(registrations);
    }

    @GetMapping("/myActivities")
    public Result<List<ActivityRegistration>> getMyActivities(
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.failed("请先登录");
        }
        
        List<ActivityRegistration> registrations = activityService.getMyActivities(userId);
        return Result.success(registrations);
    }

    @GetMapping("/clubActivities/{clubId}")
    public Result<List<Activity>> getClubActivities(@PathVariable Long clubId) {
        List<Activity> activities = activityService.getClubActivities(clubId);
        return Result.success(activities);
    }

    private Long getUserIdFromToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        token = token.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return null;
        }
        return jwtUtil.getUserIdFromToken(token);
    }
}
