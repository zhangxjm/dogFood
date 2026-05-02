package com.example.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.admin.entity.LoginUser;
import com.example.admin.entity.SysUser;
import com.example.admin.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUserName, username);
        SysUser user = userMapper.selectOne(wrapper);
        
        if (user == null) {
            log.info("登录用户：{} 不存在", username);
            throw new UsernameNotFoundException("用户名或密码错误");
        } else if ("1".equals(user.getDelFlag())) {
            log.info("登录用户：{} 已被删除", username);
            throw new UsernameNotFoundException("用户名或密码错误");
        } else if ("1".equals(user.getStatus())) {
            log.info("登录用户：{} 已被停用", username);
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        return createLoginUser(user);
    }

    public UserDetails createLoginUser(SysUser user) {
        Set<String> permissions = getMenuPermission(user);
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (String permission : permissions) {
            if (permission != null && !permission.isEmpty()) {
                authorities.add(new SimpleGrantedAuthority(permission));
            }
        }
        return new LoginUser(user, permissions, authorities);
    }

    public Set<String> getMenuPermission(SysUser user) {
        Set<String> perms = new HashSet<>();
        if (user.getUserId() == 1L) {
            perms.add("*:*:*");
            perms.add("system:user:list");
            perms.add("system:user:query");
            perms.add("system:user:add");
            perms.add("system:user:edit");
            perms.add("system:user:remove");
            perms.add("system:dept:list");
            perms.add("system:dept:query");
            perms.add("system:dept:add");
            perms.add("system:dept:edit");
            perms.add("system:dept:remove");
            perms.add("system:menu:list");
            perms.add("system:menu:query");
            perms.add("system:menu:add");
            perms.add("system:menu:edit");
            perms.add("system:menu:remove");
        } else {
            perms.add("system:user:list");
            perms.add("system:user:query");
        }
        return perms;
    }
}
