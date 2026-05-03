package com.inventory.controller;

import com.inventory.common.Result;
import com.inventory.dto.LoginDTO;
import com.inventory.security.UserPrincipal;
import com.inventory.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO loginDTO) {
        Map<String, Object> result = userService.login(loginDTO);
        return Result.success("登录成功", result);
    }

    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return Result.error(401, "未登录");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userPrincipal.getUserId());
        result.put("username", userPrincipal.getUsername());
        result.put("role", userPrincipal.getRole());
        
        return Result.success(result);
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }
}
