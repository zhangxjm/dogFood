package com.attendance.controller;

import cn.hutool.crypto.digest.BCrypt;
import com.attendance.common.PageResult;
import com.attendance.common.Result;
import com.attendance.entity.SysUser;
import com.attendance.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理 Controller
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    @GetMapping("/list")
    public Result<List<SysUser>> list(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(username)) {
            wrapper.like(SysUser::getUsername, username);
        }
        if (StringUtils.hasText(nickname)) {
            wrapper.like(SysUser::getNickname, nickname);
        }
        if (deptId != null) {
            wrapper.eq(SysUser::getDeptId, deptId);
        }
        if (status != null) {
            wrapper.eq(SysUser::getStatus, status);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        List<SysUser> list = sysUserService.list(wrapper);
        list.forEach(u -> u.setPassword(null));
        return Result.success(list);
    }

    @GetMapping("/page")
    public Result<PageResult<SysUser>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(username)) {
            wrapper.like(SysUser::getUsername, username);
        }
        if (StringUtils.hasText(nickname)) {
            wrapper.like(SysUser::getNickname, nickname);
        }
        if (deptId != null) {
            wrapper.eq(SysUser::getDeptId, deptId);
        }
        if (status != null) {
            wrapper.eq(SysUser::getStatus, status);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        
        Page<SysUser> page = new Page<>(current, size);
        sysUserService.page(page, wrapper);
        page.getRecords().forEach(u -> u.setPassword(null));
        
        PageResult<SysUser> result = new PageResult<>(
                page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<SysUser> getById(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    @PostMapping
    public Result<SysUser> save(@RequestBody SysUser user) {
        SysUser existUser = sysUserService.lambdaQuery()
                .eq(SysUser::getUsername, user.getUsername())
                .one();

        if (existUser != null) {
            return Result.error("用户名已存在");
        }

        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(BCrypt.hashpw(user.getPassword()));
        }
        user.setStatus(1);
        sysUserService.save(user);
        user.setPassword(null);
        return Result.success("新增成功", user);
    }

    @PutMapping
    public Result<SysUser> update(@RequestBody SysUser user) {
        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(BCrypt.hashpw(user.getPassword()));
        } else {
            user.setPassword(null);
        }
        sysUserService.lambdaUpdate()
                .set(StringUtils.hasText(user.getNickname()), SysUser::getNickname, user.getNickname())
                .set(user.getAvatar() != null, SysUser::getAvatar, user.getAvatar())
                .set(StringUtils.hasText(user.getEmail()), SysUser::getEmail, user.getEmail())
                .set(StringUtils.hasText(user.getPhone()), SysUser::getPhone, user.getPhone())
                .set(user.getDeptId() != null, SysUser::getDeptId, user.getDeptId())
                .set(user.getStatus() != null, SysUser::getStatus, user.getStatus())
                .set(StringUtils.hasText(user.getPassword()), SysUser::getPassword, user.getPassword())
                .eq(SysUser::getId, user.getId())
                .update();
        
        user.setPassword(null);
        return Result.success("修改成功", user);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysUserService.removeById(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        sysUserService.removeByIds(ids);
        return Result.success();
    }

    @PutMapping("/resetPassword/{id}")
    public Result<Void> resetPassword(@PathVariable Long id) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setPassword(BCrypt.hashpw("123456"));
        sysUserService.updateById(user);
        return Result.success();
    }
}
