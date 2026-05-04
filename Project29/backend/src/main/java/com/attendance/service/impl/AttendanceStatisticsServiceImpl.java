package com.attendance.service.impl;

import com.attendance.entity.AttendanceStatistics;
import com.attendance.mapper.AttendanceStatisticsMapper;
import com.attendance.service.AttendanceStatisticsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 考勤统计Service实现类
 */
@Service
public class AttendanceStatisticsServiceImpl extends ServiceImpl<AttendanceStatisticsMapper, AttendanceStatistics> implements AttendanceStatisticsService {
}
