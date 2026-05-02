package com.restaurant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.restaurant.entity.Employee;

public interface EmployeeService extends IService<Employee> {
    
    Employee getByUsername(String username);
    
    Page<Employee> pageList(Integer page, Integer pageSize, String name, String role);
    
    boolean saveEmployee(Employee employee);
    
    boolean updateEmployee(Employee employee);
    
    boolean updateStatus(Long id, Integer status);
}
