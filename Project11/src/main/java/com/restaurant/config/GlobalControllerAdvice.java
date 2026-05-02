package com.restaurant.config;

import com.restaurant.entity.Employee;
import com.restaurant.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {
    
    private final EmployeeService employeeService;
    
    @ModelAttribute
    public void addAttributes(Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() 
                    && !"anonymousUser".equals(authentication.getPrincipal())) {
                String username = authentication.getName();
                Employee employee = employeeService.getByUsername(username);
                if (employee != null) {
                    model.addAttribute("employee", employee);
                }
            }
        } catch (Exception e) {
        }
    }
}
