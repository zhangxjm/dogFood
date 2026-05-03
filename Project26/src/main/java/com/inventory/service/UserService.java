package com.inventory.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inventory.common.PageResult;
import com.inventory.dto.LoginDTO;
import com.inventory.entity.User;
import com.inventory.exception.BusinessException;
import com.inventory.mapper.UserMapper;
import com.inventory.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService extends ServiceImpl<UserMapper, User> {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public Map<String, Object> login(LoginDTO loginDTO) {
        User user = this.lambdaQuery()
                .eq(User::getUsername, loginDTO.getUsername())
                .one();
        
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        if (user.getStatus() != 1) {
            throw new BusinessException("用户已被禁用");
        }
        
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("realName", user.getRealName());
        result.put("role", user.getRole());
        
        return result;
    }

    public User getByUsername(String username) {
        return this.lambdaQuery()
                .eq(User::getUsername, username)
                .one();
    }

    public PageResult<User> pageQuery(Long current, Long size, String keyword) {
        Page<User> page = new Page<>(current, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(User::getUsername, keyword)
                    .or()
                    .like(User::getRealName, keyword);
        }
        
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> result = this.page(page, wrapper);
        
        return new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                result.getSize(),
                result.getCurrent()
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean createUser(User user) {
        User existUser = getByUsername(user.getUsername());
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(1);
        return this.save(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(User user) {
        User existUser = this.getById(user.getId());
        if (existUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(null);
        }
        
        return this.updateById(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(Long id) {
        if (id == 1) {
            throw new BusinessException("超级管理员不能删除");
        }
        return this.removeById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean resetPassword(Long id, String newPassword) {
        User user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        return this.updateById(user);
    }
}
