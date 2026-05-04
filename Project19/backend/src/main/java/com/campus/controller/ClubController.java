package com.campus.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.Result;
import com.campus.entity.Club;
import com.campus.entity.ClubMember;
import com.campus.service.ClubService;
import com.campus.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/club")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;
    private final JwtUtil jwtUtil;

    @GetMapping("/list")
    public Result<Page<Club>> listClubs(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status) {
        
        Page<Club> page = new Page<>(current, size);
        Page<Club> result = clubService.pageClubs(page, name, categoryId, status);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Club> getClubDetail(@PathVariable Long id) {
        Club club = clubService.getClubDetail(id);
        return Result.success(club);
    }

    @PostMapping("/create")
    public Result<Boolean> createClub(
            @RequestBody Club club,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.failed("请先登录");
        }
        
        boolean success = clubService.createClub(club, userId);
        return success ? Result.success("申请成功，等待审核", true) : Result.failed("创建失败");
    }

    @PostMapping("/audit/{id}")
    public Result<Boolean> auditClub(
            @PathVariable Long id,
            @RequestBody Map<String, Object> params) {
        
        Integer status = (Integer) params.get("status");
        String auditRemark = (String) params.get("auditRemark");
        
        boolean success = clubService.auditClub(id, status, auditRemark);
        return success ? Result.success("审核成功", true) : Result.failed("审核失败");
    }

    @PostMapping("/applyJoin/{clubId}")
    public Result<Boolean> applyJoin(
            @PathVariable Long clubId,
            @RequestBody(required = false) Map<String, String> params,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.failed("请先登录");
        }
        
        String remark = params != null ? params.get("remark") : null;
        boolean success = clubService.applyJoin(clubId, userId, remark);
        return success ? Result.success("申请成功", true) : Result.failed("申请失败");
    }

    @PostMapping("/auditJoin/{memberId}")
    public Result<Boolean> auditJoin(
            @PathVariable Long memberId,
            @RequestBody Map<String, Object> params) {
        
        Integer status = (Integer) params.get("status");
        String auditRemark = (String) params.get("auditRemark");
        
        boolean success = clubService.auditJoin(memberId, status, auditRemark);
        return success ? Result.success("审核成功", true) : Result.failed("审核失败");
    }

    @PostMapping("/quit/{clubId}")
    public Result<Boolean> quitClub(
            @PathVariable Long clubId,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.failed("请先登录");
        }
        
        boolean success = clubService.quitClub(clubId, userId);
        return success ? Result.success("退出成功", true) : Result.failed("退出失败");
    }

    @GetMapping("/members/{clubId}")
    public Result<List<ClubMember>> getClubMembers(@PathVariable Long clubId) {
        List<ClubMember> members = clubService.getClubMembers(clubId);
        return Result.success(members);
    }

    @GetMapping("/myClubs")
    public Result<List<ClubMember>> getMyClubs(
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.failed("请先登录");
        }
        
        List<ClubMember> members = clubService.getMyClubs(userId);
        return Result.success(members);
    }

    @GetMapping("/myApplyClubs")
    public Result<List<ClubMember>> getMyApplyClubs(
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.failed("请先登录");
        }
        
        List<ClubMember> members = clubService.getMyApplyClubs(userId);
        return Result.success(members);
    }

    @GetMapping("/pendingApprovals/{clubId}")
    public Result<List<ClubMember>> getPendingApprovals(@PathVariable Long clubId) {
        List<ClubMember> approvals = clubService.getPendingApprovals(clubId);
        return Result.success(approvals);
    }

    @PostMapping("/update")
    public Result<Boolean> updateClub(@RequestBody Club club) {
        boolean success = clubService.updateById(club);
        return success ? Result.success("更新成功", true) : Result.failed("更新失败");
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
