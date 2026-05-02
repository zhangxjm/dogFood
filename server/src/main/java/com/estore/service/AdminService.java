package com.estore.service;

import com.estore.config.JwtProperties;
import com.estore.entity.Admin;
import com.estore.repository.AdminRepository;
import com.estore.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final AdminRepository adminRepository;
    private final JwtProperties jwtProperties;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    
    public Map<String, Object> login(String username, String password) {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));
        
        if (!passwordEncoder.matches(password, admin.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        if (admin.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("adminId", admin.getId());
        claims.put("username", username);
        String token = jwtUtil.generateToken(jwtProperties.getSecret(), jwtProperties.getExpiration(), claims);
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        
        Map<String, Object> adminMap = new HashMap<>();
        adminMap.put("id", admin.getId());
        adminMap.put("username", admin.getUsername());
        adminMap.put("nickname", admin.getNickname());
        adminMap.put("avatarUrl", admin.getAvatarUrl());
        adminMap.put("status", admin.getStatus());
        result.put("admin", adminMap);
        
        return result;
    }
    
    public Admin getById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("管理员不存在"));
    }
    
    @Transactional
    public Admin create(Admin admin) {
        if (adminRepository.existsByUsername(admin.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return adminRepository.save(admin);
    }
    
    @Transactional
    public Admin update(Long id, Admin admin) {
        Admin existingAdmin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("管理员不存在"));
        
        if (admin.getNickname() != null) {
            existingAdmin.setNickname(admin.getNickname());
        }
        if (admin.getAvatarUrl() != null) {
            existingAdmin.setAvatarUrl(admin.getAvatarUrl());
        }
        if (admin.getPassword() != null && !admin.getPassword().isEmpty()) {
            existingAdmin.setPassword(passwordEncoder.encode(admin.getPassword()));
        }
        if (admin.getStatus() != null) {
            existingAdmin.setStatus(admin.getStatus());
        }
        
        return adminRepository.save(existingAdmin);
    }
}
