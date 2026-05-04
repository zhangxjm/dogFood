package com.attendance.service.impl;

import com.attendance.entity.SysUserRole;
import com.attendance.mapper.SysUserRoleMapper;
import com.attendance.service.SysUserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 用户角色关联Service实现类
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {
}