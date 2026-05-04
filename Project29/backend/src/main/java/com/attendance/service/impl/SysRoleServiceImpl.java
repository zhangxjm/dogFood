package com.attendance.service.impl;

import com.attendance.entity.SysRole;
import com.attendance.mapper.SysRoleMapper;
import com.attendance.service.SysRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 角色Service实现类
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
}
