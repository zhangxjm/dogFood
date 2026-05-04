package com.attendance.service.impl;

import com.attendance.entity.OvertimeApplication;
import com.attendance.mapper.OvertimeApplicationMapper;
import com.attendance.service.OvertimeApplicationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 加班申请Service实现类
 */
@Service
public class OvertimeApplicationServiceImpl extends ServiceImpl<OvertimeApplicationMapper, OvertimeApplication> implements OvertimeApplicationService {
}
