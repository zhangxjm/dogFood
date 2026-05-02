package com.restaurant.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restaurant.entity.Employee;
import com.restaurant.mapper.EmployeeMapper;
import com.restaurant.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
    
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public Employee getByUsername(String username) {
        if (StrUtil.isBlank(username)) {
            return null;
        }
        return this.lambdaQuery()
                .eq(Employee::getUsername, username)
                .one();
    }
    
    @Override
    public Page<Employee> pageList(Integer page, Integer pageSize, String name, String role) {
        Page<Employee> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(name)) {
            wrapper.like(Employee::getName, name);
        }
        if (StrUtil.isNotBlank(role)) {
            wrapper.eq(Employee::getRole, role);
        }
        wrapper.orderByDesc(Employee::getCreateTime);
        
        return this.page(pageParam, wrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveEmployee(Employee employee) {
        if (StrUtil.isBlank(employee.getPassword())) {
            employee.setPassword("123456");
        }
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        employee.setStatus(Employee.STATUS_ENABLED);
        return this.save(employee);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateEmployee(Employee employee) {
        if (StrUtil.isNotBlank(employee.getPassword())) {
            employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        } else {
            employee.setPassword(null);
        }
        return this.updateById(employee);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer status) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setStatus(status);
        return this.updateById(employee);
    }
}
