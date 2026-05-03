package com.campus.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.Result;
import com.campus.entity.User;
import com.campus.service.UserService;
import com.campus.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        
        User user = userService.login(username, password);
        
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);
        
        return Result.success("登录成功", data);
    }

    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        User registered = userService.register(user);
        return Result.success("注册成功", registered);
    }

    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.failed("请先登录");
        }
        
        token = token.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return Result.failed("token已过期");
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userService.getById(userId);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    @GetMapping("/list")
    public Result<Page<User>> listUsers(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) Integer role,
            @RequestParam(required = false) Integer status) {
        
        Page<User> page = new Page<>(current, size);
        Page<User> result = userService.pageUsers(page, username, realName, role, status);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    @PostMapping("/update")
    public Result<Boolean> updateUser(@RequestBody User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            throw new RuntimeException("请使用修改密码接口");
        }
        user.setPassword(null);
        boolean success = userService.updateById(user);
        return success ? Result.success("更新成功", true) : Result.failed("更新失败");
    }

    @PostMapping("/updateStatus/{id}")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = userService.updateStatus(id, status);
        return success ? Result.success("更新成功", true) : Result.failed("更新失败");
    }

    @PostMapping("/updateRole/{id}")
    public Result<Boolean> updateRole(@PathVariable Long id, @RequestParam Integer role) {
        boolean success = userService.updateRole(id, role);
        return success ? Result.success("更新成功", true) : Result.failed("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteUser(@PathVariable Long id) {
        boolean success = userService.removeById(id);
        return success ? Result.success("删除成功", true) : Result.failed("删除失败");
    }
}
