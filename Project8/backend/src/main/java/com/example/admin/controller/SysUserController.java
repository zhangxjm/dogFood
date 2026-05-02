package com.example.admin.controller;

import com.example.admin.common.Result;
import com.example.admin.entity.LoginUser;
import com.example.admin.entity.SysUser;
import com.example.admin.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/system/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService userService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:user:list')")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            SysUser user) {
        return userService.selectUserPage(user, pageNum, pageSize);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('system:user:query')")
    public Result<SysUser> getInfo(@PathVariable Long userId) {
        return userService.selectUserById(userId);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:user:add')")
    public Result<Void> add(@RequestBody SysUser user, @AuthenticationPrincipal LoginUser loginUser) {
        user.setCreateBy(loginUser.getUser().getUserName());
        return userService.insertUser(user);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result<Void> edit(@RequestBody SysUser user, @AuthenticationPrincipal LoginUser loginUser) {
        user.setUpdateBy(loginUser.getUser().getUserName());
        return userService.updateUser(user);
    }

    @DeleteMapping("/{userIds}")
    @PreAuthorize("hasAuthority('system:user:remove')")
    public Result<Void> remove(@PathVariable Long[] userIds) {
        return userService.deleteUserByIds(userIds);
    }

    @PutMapping("/resetPwd")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result<Void> resetPwd(@RequestBody Map<String, Object> params) {
        Long userId = ((Number) params.get("userId")).longValue();
        String password = (String) params.get("password");
        return userService.resetPwd(userId, password);
    }

    @PutMapping("/changeStatus")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result<Void> changeStatus(@RequestBody SysUser user) {
        return userService.updateStatus(user.getUserId(), user.getStatus());
    }

    @GetMapping("/profile")
    public Result<SysUser> profile(@AuthenticationPrincipal LoginUser loginUser) {
        return Result.success(loginUser.getUser());
    }
}
