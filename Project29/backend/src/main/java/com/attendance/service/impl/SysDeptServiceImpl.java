package com.attendance.service.impl;

import com.attendance.entity.SysDept;
import com.attendance.mapper.SysDeptMapper;
import com.attendance.service.SysDeptService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 部门Service实现类
 */
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {
}
