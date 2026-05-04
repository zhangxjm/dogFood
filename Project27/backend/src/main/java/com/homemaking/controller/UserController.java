package com.homemaking.controller;

import com.homemaking.common.Result;
import com.homemaking.entity.SysUser;
import com.homemaking.mapper.SysUserMapper;
import com.homemaking.security.JwtTokenUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private SysUserMapper userMapper;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("/profile")
    public Result<SysUser> getProfile(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        Long userId = jwtTokenUtil.getUserIdFromToken(jwt);
        
        SysUser user = userMapper.selectById(userId);
        user.setPassword(null);
        return Result.success(user);
    }
    
    @PutMapping("/profile")
    public Result<String> updateProfile(@RequestBody UpdateProfileRequest request,
                                          @RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        Long userId = jwtTokenUtil.getUserIdFromToken(jwt);
        
        SysUser user = userMapper.selectById(userId);
        
        if (request.getRealName() != null) {
            user.setRealName(request.getRealName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        
        userMapper.updateById(user);
        return Result.success("更新成功");
    }
    
    @PutMapping("/password")
    public Result<String> updatePassword(@RequestBody UpdatePasswordRequest request,
                                           @RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        Long userId = jwtTokenUtil.getUserIdFromToken(jwt);
        
        SysUser user = userMapper.selectById(userId);
        
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return Result.error("原密码错误");
        }
        
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userMapper.updateById(user);
        
        return Result.success("密码修改成功");
    }
    
    @Data
    public static class UpdateProfileRequest {
        private String realName;
        private String phone;
        private String avatar;
    }
    
    @Data
    public static class UpdatePasswordRequest {
        private String oldPassword;
        private String newPassword;
    }
}
