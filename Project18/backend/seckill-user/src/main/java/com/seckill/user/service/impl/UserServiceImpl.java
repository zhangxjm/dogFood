package com.seckill.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seckill.common.entity.User;
import com.seckill.common.exception.BusinessException;
import com.seckill.common.util.JwtUtils;
import com.seckill.user.dto.LoginDTO;
import com.seckill.user.dto.LoginVO;
import com.seckill.user.dto.RegisterDTO;
import com.seckill.user.mapper.UserMapper;
import com.seckill.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public LoginVO login(LoginDTO dto) {
        User user = getByUsername(dto.getUsername());
        
        if (user == null) {
            throw BusinessException.of("用户名或密码错误");
        }
        
        if (user.getStatus() != 1) {
            throw BusinessException.of("账号已被禁用");
        }
        
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw BusinessException.of("用户名或密码错误");
        }
        
        user.setLastLoginTime(LocalDateTime.now());
        updateById(user);
        
        String token = JwtUtils.generateToken(user.getId(), user.getUsername());
        
        redisTemplate.opsForValue().set("user:token:" + user.getId(), token, 7, TimeUnit.DAYS);
        
        log.info("用户登录成功: userId={}, username={}", user.getId(), user.getUsername());
        
        return LoginVO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname() != null ? user.getNickname() : user.getUsername())
                .avatar(user.getAvatar())
                .token(token)
                .role("user")
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO dto) {
        if (getByUsername(dto.getUsername()) != null) {
            throw BusinessException.of("用户名已存在");
        }
        
        if (getByPhone(dto.getPhone()) != null) {
            throw BusinessException.of("手机号已被注册");
        }
        
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setStatus(1);
        
        save(user);
        
        log.info("用户注册成功: userId={}, username={}", user.getId(), user.getUsername());
    }

    @Override
    public LoginVO getCurrentUser(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw BusinessException.of("用户不存在");
        }
        
        return LoginVO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname() != null ? user.getNickname() : user.getUsername())
                .avatar(user.getAvatar())
                .role("user")
                .build();
    }

    @Override
    public User getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .last("LIMIT 1"));
    }

    @Override
    public User getByPhone(String phone) {
        return getOne(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, phone)
                .last("LIMIT 1"));
    }
}
