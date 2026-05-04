package com.attendance.mapper;

import com.attendance.entity.LeaveApplication;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 请假申请Mapper接口
 */
@Mapper
public interface LeaveApplicationMapper extends BaseMapper<LeaveApplication> {
}
