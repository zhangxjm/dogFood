package com.attendance.service.impl;

import com.attendance.entity.SysPost;
import com.attendance.mapper.SysPostMapper;
import com.attendance.service.SysPostService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 岗位Service实现类
 */
@Service
public class SysPostServiceImpl extends ServiceImpl<SysPostMapper, SysPost> implements SysPostService {
}
