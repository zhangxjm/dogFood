package com.attendance.controller;

import cn.hutool.crypto.digest.BCrypt;
import com.attendance.common.Result;
import com.attendance.entity.SysUser;
import com.attendance.service.SysUserService;
import com.attendance.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录认证 Controller
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserService sysUserService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginForm) {
        String username = loginForm.get("username");
        String password = loginForm.get("password");

        if (username == null || password == null) {
            return Result.error("用户名或密码不能为空");
        }

        SysUser user = sysUserService.lambdaQuery()
                .eq(SysUser::getUsername, username)
                .one();

        if (user == null) {
            return Result.error("用户不存在");
        }

        if (user.getStatus() != 1) {
            return Result.error("用户已被禁用");
        }

        if (!BCrypt.checkpw(password, user.getPassword())) {
            return Result.error("密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());
        result.put("avatar", user.getAvatar());

        return Result.success("登录成功", result);
    }

    @PostMapping("/register")
    public Result<SysUser> register(@RequestBody SysUser user) {
        SysUser existUser = sysUserService.lambdaQuery()
                .eq(SysUser::getUsername, user.getUsername())
                .one();

        if (existUser != null) {
            return Result.error("用户名已存在");
        }

        user.setPassword(BCrypt.hashpw(user.getPassword()));
        user.setStatus(1);
        sysUserService.save(user);
        user.setPassword(null);
        return Result.success("注册成功", user);
    }

    @GetMapping("/info")
    public Result<SysUser> getInfo(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error(401, "未登录");
        }

        token = token.substring(7);
        if (jwtUtil.isTokenExpired(token)) {
            return Result.error(401, "Token已过期");
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        SysUser user = sysUserService.getById(userId);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }
}
