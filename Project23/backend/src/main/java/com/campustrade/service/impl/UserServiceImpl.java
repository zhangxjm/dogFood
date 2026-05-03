package com.campustrade.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campustrade.entity.User;
import com.campustrade.mapper.UserMapper;
import com.campustrade.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User login(String username, String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return null;
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = this.getOne(wrapper);

        if (user == null) {
            return null;
        }

        if (BCrypt.checkpw(password, user.getPassword())) {
            user.setPassword(null);
            return user;
        }

        return null;
    }

    @Override
    public boolean register(User user) {
        if (user == null || !StringUtils.hasText(user.getUsername()) || !StringUtils.hasText(user.getPassword())) {
            return false;
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        if (this.count(wrapper) > 0) {
            return false;
        }

        user.setPassword(BCrypt.hashpw(user.getPassword()));

        if (user.getNickname() == null) {
            user.setNickname(user.getUsername());
        }
        if (user.getRole() == null) {
            user.setRole("user");
        }
        if (user.getStatus() == null) {
            user.setStatus(1);
        }

        return this.save(user);
    }

    @Override
    public User getByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return null;
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = this.getOne(wrapper);

        if (user != null) {
            user.setPassword(null);
        }

        return user;
    }

    @Override
    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        if (userId == null || !StringUtils.hasText(oldPassword) || !StringUtils.hasText(newPassword)) {
            return false;
        }

        User user = this.getById(userId);
        if (user == null) {
            return false;
        }

        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            return false;
        }

        user.setPassword(BCrypt.hashpw(newPassword));

        return this.updateById(user);
    }
}
