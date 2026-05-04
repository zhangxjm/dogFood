package com.attendance.service.impl;

import com.attendance.entity.EmpEmployee;
import com.attendance.mapper.EmpEmployeeMapper;
import com.attendance.service.EmpEmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 员工档案Service实现类
 */
@Service
public class EmpEmployeeServiceImpl extends ServiceImpl<EmpEmployeeMapper, EmpEmployee> implements EmpEmployeeService {
}
