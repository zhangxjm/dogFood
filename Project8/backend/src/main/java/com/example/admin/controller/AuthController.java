package com.example.admin.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.example.admin.common.Result;
import com.example.admin.entity.LoginUser;
import com.example.admin.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest request) {
        log.info("用户登录请求: {}", request.getUsername());
        return authService.login(request.getUsername(), request.getPassword());
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        return authService.logout();
    }

    @GetMapping("/getInfo")
    public Result<Map<String, Object>> getInfo(@AuthenticationPrincipal LoginUser loginUser) {
        return authService.getInfo(loginUser);
    }

    @GetMapping("/captcha")
    public Result<Map<String, String>> getCaptcha(HttpServletRequest request) {
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(110, 40, 4, 20);
        String captchaText = captcha.getCode();
        
        String uuid = UUID.randomUUID().toString();
        request.getSession().setAttribute("captcha_" + uuid, captchaText);
        
        String base64Image = Base64.getEncoder().encodeToString(captcha.getImageBytes());
        
        Map<String, String> result = new HashMap<>();
        result.put("uuid", uuid);
        result.put("img", "data:image/png;base64," + base64Image);
        
        return Result.success(result);
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
        private String uuid;
        private String code;
    }
}
