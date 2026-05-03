package com.seckill.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seckill.common.entity.User;
import com.seckill.user.dto.LoginDTO;
import com.seckill.user.dto.LoginVO;
import com.seckill.user.dto.RegisterDTO;

public interface UserService extends IService<User> {

    LoginVO login(LoginDTO dto);

    void register(RegisterDTO dto);

    LoginVO getCurrentUser(Long userId);

    User getByUsername(String username);

    User getByPhone(String phone);
}
