package com.campustrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campustrade.entity.User;

public interface UserService extends IService<User> {
    User login(String username, String password);
    boolean register(User user);
    User getByUsername(String username);
    boolean updatePassword(Long userId, String oldPassword, String newPassword);
}
