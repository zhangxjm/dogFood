package com.inventory.controller;

import com.inventory.common.Result;
import com.inventory.entity.User;
import com.inventory.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class InitController {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/reset-admin-password")
    public Result<String> resetAdminPassword(@RequestParam String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            return Result.error("密码长度至少6位");
        }
        
        User admin = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                        .eq(User::getUsername, "admin")
        );
        
        if (admin == null) {
            return Result.error("管理员用户不存在");
        }
        
        admin.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(admin);
        
        return Result.success("密码已重置为: " + newPassword);
    }
}
