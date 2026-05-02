package com.example.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.admin.common.Result;
import com.example.admin.entity.SysUser;
import com.example.admin.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserService extends ServiceImpl<SysUserMapper, SysUser> {

    private final PasswordEncoder passwordEncoder;

    public Result<Map<String, Object>> selectUserPage(SysUser user, int pageNum, int pageSize) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(user.getUserName())) {
            wrapper.like(SysUser::getUserName, user.getUserName());
        }
        if (StringUtils.hasText(user.getStatus())) {
            wrapper.eq(SysUser::getStatus, user.getStatus());
        }
        if (StringUtils.hasText(user.getPhonenumber())) {
            wrapper.like(SysUser::getPhonenumber, user.getPhonenumber());
        }
        if (user.getDeptId() != null) {
            wrapper.eq(SysUser::getDeptId, user.getDeptId());
        }
        
        wrapper.orderByDesc(SysUser::getCreateTime);
        Page<SysUser> result = this.page(page, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", result.getRecords());
        data.put("total", result.getTotal());
        data.put("pageNum", result.getCurrent());
        data.put("pageSize", result.getSize());
        
        return Result.success(data);
    }

    public Result<SysUser> selectUserById(Long userId) {
        SysUser user = this.getById(userId);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    @Transactional
    public Result<Void> insertUser(SysUser user) {
        if (checkUserNameUnique(user)) {
            return Result.error("用户名已存在");
        }
        if (checkPhoneUnique(user)) {
            return Result.error("手机号已存在");
        }
        if (checkEmailUnique(user)) {
            return Result.error("邮箱已存在");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        this.save(user);
        return Result.success();
    }

    @Transactional
    public Result<Void> updateUser(SysUser user) {
        SysUser existingUser = this.getById(user.getUserId());
        if (existingUser == null) {
            return Result.error("用户不存在");
        }
        
        if (!existingUser.getUserName().equals(user.getUserName()) && checkUserNameUnique(user)) {
            return Result.error("用户名已存在");
        }
        
        user.setUpdateTime(LocalDateTime.now());
        user.setPassword(null);
        
        this.updateById(user);
        return Result.success();
    }

    @Transactional
    public Result<Void> deleteUserByIds(Long[] userIds) {
        if (Arrays.asList(userIds).contains(1L)) {
            return Result.error("不能删除超级管理员");
        }
        this.removeByIds(Arrays.asList(userIds));
        return Result.success();
    }

    @Transactional
    public Result<Void> resetPwd(Long userId, String newPassword) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        this.updateById(user);
        return Result.success();
    }

    @Transactional
    public Result<Void> updateStatus(Long userId, String status) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        this.updateById(user);
        return Result.success();
    }

    public void recordLoginInfo(SysUser user) {
        user.setLoginDate(LocalDateTime.now());
        this.updateById(user);
    }

    private boolean checkUserNameUnique(SysUser user) {
        Long userId = user.getUserId() == null ? -1L : user.getUserId();
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUserName, user.getUserName());
        SysUser existUser = this.getOne(wrapper);
        return existUser != null && !existUser.getUserId().equals(userId);
    }

    private boolean checkPhoneUnique(SysUser user) {
        if (!StringUtils.hasText(user.getPhonenumber())) {
            return false;
        }
        Long userId = user.getUserId() == null ? -1L : user.getUserId();
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getPhonenumber, user.getPhonenumber());
        SysUser existUser = this.getOne(wrapper);
        return existUser != null && !existUser.getUserId().equals(userId);
    }

    private boolean checkEmailUnique(SysUser user) {
        if (!StringUtils.hasText(user.getEmail())) {
            return false;
        }
        Long userId = user.getUserId() == null ? -1L : user.getUserId();
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getEmail, user.getEmail());
        SysUser existUser = this.getOne(wrapper);
        return existUser != null && !existUser.getUserId().equals(userId);
    }
}
