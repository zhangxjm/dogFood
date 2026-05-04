package com.attendance.service.impl;

import com.attendance.entity.AttendanceRecord;
import com.attendance.mapper.AttendanceRecordMapper;
import com.attendance.service.AttendanceRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 考勤记录Service实现类
 */
@Service
public class AttendanceRecordServiceImpl extends ServiceImpl<AttendanceRecordMapper, AttendanceRecord> implements AttendanceRecordService {
}
