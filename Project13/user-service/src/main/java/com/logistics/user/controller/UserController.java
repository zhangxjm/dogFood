package com.logistics.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.logistics.common.dto.UserLoginDTO;
import com.logistics.common.dto.UserRegisterDTO;
import com.logistics.common.entity.User;
import com.logistics.common.result.Result;
import com.logistics.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<User> login(@Valid @RequestBody UserLoginDTO dto) {
        User user = userService.login(dto);
        user.setPassword(null);
        return Result.success("登录成功", user);
    }

    @PostMapping("/register")
    public Result<User> register(@Valid @RequestBody UserRegisterDTO dto) {
        User user = userService.register(dto);
        user.setPassword(null);
        return Result.success("注册成功", user);
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable("id") Long id) {
        User user = userService.getById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    @GetMapping("/username/{username}")
    public Result<User> getByUsername(@PathVariable("username") String username) {
        User user = userService.getByUsername(username);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    @GetMapping("/page")
    public Result<Page<User>> pageList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        Page<User> page = userService.pageList(current, size, keyword);
        page.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(page);
    }

    @PostMapping("/save")
    public Result<Void> saveOrUpdate(@RequestBody User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(null);
        }
        userService.saveOrUpdate(user);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        userService.removeById(id);
        return Result.success();
    }
}
