package com.campus.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.entity.User;

public interface UserService extends IService<User> {

    User login(String username, String password);

    User register(User user);

    Page<User> pageUsers(Page<User> page, String username, String realName, Integer role, Integer status);

    User getUserByUsername(String username);

    boolean updateStatus(Long id, Integer status);

    boolean updateRole(Long id, Integer role);
}
