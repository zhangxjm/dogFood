package com.example.admin.service;

import com.example.admin.common.Result;
import com.example.admin.entity.LoginUser;
import com.example.admin.entity.SysUser;
import com.example.admin.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final SysUserService userService;

    public Result<Map<String, Object>> login(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
            
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();
            SysUser user = loginUser.getUser();
            
            userService.recordLoginInfo(user);
            
            String token = jwtUtils.generateToken(user.getUserName(), user.getUserId());
            
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("user", user);
            
            log.info("用户 {} 登录成功", username);
            return Result.success("登录成功", result);
        } catch (Exception e) {
            log.error("登录失败: {}", e.getMessage());
            return Result.error(401, "用户名或密码错误");
        }
    }

    public Result<Void> logout() {
        return Result.success();
    }

    public Result<Map<String, Object>> getInfo(LoginUser loginUser) {
        SysUser user = loginUser.getUser();
        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("roles", new String[]{"admin"});
        result.put("permissions", loginUser.getPermissions());
        return Result.success(result);
    }
}
