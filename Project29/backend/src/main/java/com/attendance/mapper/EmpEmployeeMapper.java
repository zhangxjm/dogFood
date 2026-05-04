package com.attendance.mapper;

import com.attendance.entity.EmpEmployee;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 员工档案Mapper接口
 */
@Mapper
public interface EmpEmployeeMapper extends BaseMapper<EmpEmployee> {
}
