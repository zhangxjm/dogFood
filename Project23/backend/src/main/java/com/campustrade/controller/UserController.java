package com.campustrade.controller;

import com.campustrade.common.JwtUtils;
import com.campustrade.common.Result;
import com.campustrade.entity.User;
import com.campustrade.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        
        User user = userService.login(username, password);
        if (user == null) {
            return Result.error("用户名或密码错误");
        }
        
        if (user.getStatus() != 1) {
            return Result.error("账号已被禁用");
        }
        
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        
        return Result.success("登录成功", result);
    }

    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        boolean success = userService.register(user);
        if (success) {
            user.setPassword(null);
            return Result.success("注册成功", user);
        }
        return Result.error("用户名已存在");
    }

    @GetMapping("/info")
    public Result<User> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        
        User user = userService.getById(userId);
        if (user != null) {
            user.setPassword(null);
            return Result.success(user);
        }
        return Result.error("用户不存在");
    }

    @PutMapping("/update")
    public Result<User> updateUser(HttpServletRequest request, @RequestBody User user) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        
        user.setId(userId);
        user.setUsername(null);
        user.setPassword(null);
        user.setRole(null);
        user.setStatus(null);
        
        boolean success = userService.updateById(user);
        if (success) {
            User updatedUser = userService.getById(userId);
            updatedUser.setPassword(null);
            return Result.success("更新成功", updatedUser);
        }
        return Result.error("更新失败");
    }

    @PostMapping("/update-password")
    public Result<Void> updatePassword(HttpServletRequest request, @RequestBody Map<String, String> params) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        
        boolean success = userService.updatePassword(userId, oldPassword, newPassword);
        if (success) {
            return Result.success("密码修改成功");
        }
        return Result.error("原密码错误");
    }
}
