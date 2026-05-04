package com.campus.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.Result;
import com.campus.entity.Notification;
import com.campus.service.NotificationService;
import com.campus.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;

    @GetMapping("/list")
    public Result<List<Notification>> list(
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.failed("请先登录");
        }
        
        List<Notification> notifications = notificationService.listByUserId(userId);
        return Result.success(notifications);
    }

    @GetMapping("/page")
    public Result<Page<Notification>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Boolean unreadOnly,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.failed("请先登录");
        }
        
        Page<Notification> page = new Page<>(current, size);
        Page<Notification> result = notificationService.pageByUserId(page, userId, type, unreadOnly);
        return Result.success(result);
    }

    @GetMapping("/unreadCount")
    public Result<Map<String, Object>> getUnreadCount(
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("count", 0);
            return Result.success(result);
        }
        
        int count = notificationService.countUnread(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        return Result.success(result);
    }

    @PostMapping("/read/{id}")
    public Result<Boolean> markAsRead(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.failed("请先登录");
        }
        
        boolean success = notificationService.markAsRead(id, userId);
        return success ? Result.success("标记成功", true) : Result.failed("标记失败");
    }

    @PostMapping("/readAll")
    public Result<Boolean> markAllAsRead(
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.failed("请先登录");
        }
        
        boolean success = notificationService.markAllAsRead(userId);
        return success ? Result.success("已全部标为已读", true) : Result.failed("操作失败");
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
