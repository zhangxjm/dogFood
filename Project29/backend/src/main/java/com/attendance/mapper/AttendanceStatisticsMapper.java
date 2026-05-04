package com.attendance.mapper;

import com.attendance.entity.AttendanceStatistics;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 考勤统计Mapper接口
 */
@Mapper
public interface AttendanceStatisticsMapper extends BaseMapper<AttendanceStatistics> {
}
