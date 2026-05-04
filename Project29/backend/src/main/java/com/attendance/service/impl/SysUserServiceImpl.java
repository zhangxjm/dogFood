package com.attendance.service.impl;

import com.attendance.entity.SysUser;
import com.attendance.mapper.SysUserMapper;
import com.attendance.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 用户Service实现类
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
}
