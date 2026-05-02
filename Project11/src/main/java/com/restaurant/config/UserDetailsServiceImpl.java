package com.restaurant.config;

import com.restaurant.entity.Employee;
import com.restaurant.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final EmployeeService employeeService;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employeeService.getByUsername(username);
        
        if (employee == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        
        if (Employee.STATUS_DISABLED.equals(employee.getStatus())) {
            throw new UsernameNotFoundException("用户已被禁用: " + username);
        }
        
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + employee.getRole()));
        
        return new User(
                employee.getUsername(),
                employee.getPassword(),
                authorities
        );
    }
}
