package com.campus.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.Result;
import com.campus.entity.ClubAnnouncement;
import com.campus.service.ClubAnnouncementService;
import com.campus.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcement")
@RequiredArgsConstructor
public class ClubAnnouncementController {

    private final ClubAnnouncementService announcementService;
    private final JwtUtil jwtUtil;

    @GetMapping("/club/{clubId}")
    public Result<List<ClubAnnouncement>> listByClubId(@PathVariable Long clubId) {
        List<ClubAnnouncement> announcements = announcementService.listByClubId(clubId);
        return Result.success(announcements);
    }

    @GetMapping("/club/{clubId}/page")
    public Result<Page<ClubAnnouncement>> pageByClubId(
            @PathVariable Long clubId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer type) {
        
        Page<ClubAnnouncement> page = new Page<>(current, size);
        Page<ClubAnnouncement> result = announcementService.pageByClubId(page, clubId, type);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<ClubAnnouncement> getDetail(@PathVariable Long id) {
        ClubAnnouncement announcement = announcementService.getByIdWithDetail(id);
        if (announcement != null) {
            announcementService.incrementReadCount(id);
        }
        return Result.success(announcement);
    }

    @PostMapping("/create")
    public Result<Boolean> create(
            @RequestBody ClubAnnouncement announcement,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.failed("请先登录");
        }
        
        boolean success = announcementService.createAnnouncement(announcement, userId);
        return success ? Result.success("发布成功", true) : Result.failed("发布失败");
    }

    @PostMapping("/update")
    public Result<Boolean> update(
            @RequestBody ClubAnnouncement announcement,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.failed("请先登录");
        }
        
        boolean success = announcementService.updateAnnouncement(announcement, userId);
        return success ? Result.success("更新成功", true) : Result.failed("更新失败");
    }

    @PostMapping("/delete/{id}")
    public Result<Boolean> delete(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.failed("请先登录");
        }
        
        boolean success = announcementService.deleteAnnouncement(id, userId);
        return success ? Result.success("删除成功", true) : Result.failed("删除失败");
    }

    @PostMapping("/toggleTop/{id}")
    public Result<Boolean> toggleTop(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.failed("请先登录");
        }
        
        boolean success = announcementService.toggleTop(id, userId);
        return success ? Result.success("操作成功", true) : Result.failed("操作失败");
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
