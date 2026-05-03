package com.jobrecruitment.service;

import cn.hutool.crypto.digest.MD5;
import com.jobrecruitment.context.UserContext;
import com.jobrecruitment.dto.LoginDTO;
import com.jobrecruitment.dto.LoginVO;
import com.jobrecruitment.dto.PageDTO;
import com.jobrecruitment.dto.RegisterDTO;
import com.jobrecruitment.entity.User;
import com.jobrecruitment.exception.BusinessException;
import com.jobrecruitment.mapper.UserMapper;
import com.jobrecruitment.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final MD5 md5 = MD5.create();

    public LoginVO login(LoginDTO dto) {
        User user = userMapper.createLambdaQuery()
                .andEq(User::getUsername, dto.getUsername())
                .single();

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        String encryptedPassword = md5.digestHex(dto.getPassword());
        if (!encryptedPassword.equals(user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        if (user.getStatus() == User.STATUS_DISABLED) {
            throw new BusinessException("账户已被禁用");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        return LoginVO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .token(token)
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO dto) {
        User existUser = userMapper.createLambdaQuery()
                .andEq(User::getUsername, dto.getUsername())
                .single();

        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(md5.digestHex(dto.getPassword()));
        user.setRole(dto.getRole());
        user.setStatus(dto.getRole() == User.ROLE_HR ? User.STATUS_PENDING : User.STATUS_NORMAL);

        userMapper.insert(user);
        log.info("用户注册成功: {}", user.getUsername());
    }

    public User getCurrentUser() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }
        User user = userMapper.single(userId);
        user.setPassword(null);
        return user;
    }

    public User getById(Long id) {
        return userMapper.single(id);
    }

    public List<User> list(PageDTO dto) {
        var query = userMapper.createLambdaQuery();
        if (dto.getStatus() != null) {
            query.andEq(User::getStatus, dto.getStatus());
        }
        return query.orderByDesc(User::getCreateTime)
                .limit((dto.getPageNum() - 1) * dto.getPageSize(), dto.getPageSize())
                .select();
    }

    public Long count(PageDTO dto) {
        var query = userMapper.createLambdaQuery();
        if (dto.getStatus() != null) {
            query.andEq(User::getStatus, dto.getStatus());
        }
        return query.count();
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        User user = userMapper.single(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }
}
