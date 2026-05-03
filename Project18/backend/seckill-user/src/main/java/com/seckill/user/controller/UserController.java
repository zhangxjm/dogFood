package com.seckill.user.controller;

import com.seckill.common.annotation.PreventDuplicateSubmit;
import com.seckill.common.annotation.RateLimiter;
import com.seckill.common.result.Result;
import com.seckill.user.dto.LoginDTO;
import com.seckill.user.dto.LoginVO;
import com.seckill.user.dto.RegisterDTO;
import com.seckill.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户接口", description = "用户注册、登录、个人信息等接口")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "用户登录")
    @RateLimiter(limit = 5, windowTime = 1, message = "登录过于频繁，请稍后再试")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        LoginVO vo = userService.login(dto);
        return Result.success("登录成功", vo);
    }

    @Operation(summary = "用户注册")
    @PreventDuplicateSubmit(interval = 5)
    @RateLimiter(limit = 3, windowTime = 1, message = "注册过于频繁，请稍后再试")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        userService.register(dto);
        return Result.success("注册成功", null);
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<LoginVO> getCurrentUser(@RequestHeader("X-User-Id") Long userId) {
        LoginVO vo = userService.getCurrentUser(userId);
        return Result.success(vo);
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("X-User-Id") Long userId) {
        return Result.success("登出成功", null);
    }
}
