package com.restaurant.config;

import com.restaurant.entity.Employee;
import com.restaurant.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final EmployeeService employeeService;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        String defaultPassword = "admin123";
        String encodedPassword = passwordEncoder.encode(defaultPassword);
        
        log.info("默认密码 BCrypt 哈希: {}", encodedPassword);
        
        updateEmployeePassword("admin", defaultPassword, encodedPassword);
        updateEmployeePassword("cashier", defaultPassword, encodedPassword);
        updateEmployeePassword("kitchen", defaultPassword, encodedPassword);
        
        log.info("数据初始化完成");
    }
    
    private void updateEmployeePassword(String username, String rawPassword, String encodedPassword) {
        Employee employee = employeeService.getByUsername(username);
        if (employee != null) {
            employee.setPassword(encodedPassword);
            employeeService.updateById(employee);
            log.info("已更新用户 '{}' 密码为: {}", username, rawPassword);
        } else {
            log.warn("用户 '{}' 不存在", username);
        }
    }
}
