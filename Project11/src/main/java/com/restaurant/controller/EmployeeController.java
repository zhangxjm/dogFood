package com.restaurant.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.restaurant.common.Result;
import com.restaurant.entity.Employee;
import com.restaurant.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/employee")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class EmployeeController {
    
    private final EmployeeService employeeService;
    
    @GetMapping
    public String list() {
        return "employee/list";
    }
    
    @GetMapping("/list")
    @ResponseBody
    public Result<Page<Employee>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String role) {
        
        Page<Employee> result = employeeService.pageList(page, pageSize, name, role);
        return Result.success(result);
    }
    
    @GetMapping("/add")
    public String add() {
        return "employee/add";
    }
    
    @PostMapping("/save")
    @ResponseBody
    public Result<Boolean> save(@RequestBody Employee employee) {
        if (employeeService.getByUsername(employee.getUsername()) != null) {
            return Result.error("用户名已存在");
        }
        boolean result = employeeService.saveEmployee(employee);
        return result ? Result.success(true) : Result.error("保存失败");
    }
    
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Employee employee = employeeService.getById(id);
        model.addAttribute("employee", employee);
        return "employee/edit";
    }
    
    @PostMapping("/update")
    @ResponseBody
    public Result<Boolean> update(@RequestBody Employee employee) {
        boolean result = employeeService.updateEmployee(employee);
        return result ? Result.success(true) : Result.error("更新失败");
    }
    
    @PostMapping("/status")
    @ResponseBody
    public Result<Boolean> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        boolean result = employeeService.updateStatus(id, status);
        return result ? Result.success(true) : Result.error("更新失败");
    }
    
    @GetMapping("/check-username")
    @ResponseBody
    public Result<Boolean> checkUsername(@RequestParam String username, 
                                          @RequestParam(required = false) Long excludeId) {
        Employee employee = employeeService.getByUsername(username);
        if (employee == null) {
            return Result.success(true);
        }
        if (excludeId != null && employee.getId().equals(excludeId)) {
            return Result.success(true);
        }
        return Result.success(false);
    }
}
