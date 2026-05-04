package com.attendance.service.impl;

import com.attendance.entity.LeaveApplication;
import com.attendance.mapper.LeaveApplicationMapper;
import com.attendance.service.LeaveApplicationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 请假申请Service实现类
 */
@Service
public class LeaveApplicationServiceImpl extends ServiceImpl<LeaveApplicationMapper, LeaveApplication> implements LeaveApplicationService {
}
