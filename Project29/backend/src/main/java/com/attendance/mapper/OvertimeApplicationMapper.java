package com.attendance.mapper;

import com.attendance.entity.OvertimeApplication;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 加班申请Mapper接口
 */
@Mapper
public interface OvertimeApplicationMapper extends BaseMapper<OvertimeApplication> {
}
