package com.estore.controller;

import com.estore.common.Result;
import com.estore.entity.Admin;
import com.estore.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final AdminService adminService;
    
    @GetMapping("/info")
    public Result<Admin> getInfo(@RequestAttribute("adminId") Long adminId) {
        try {
            Admin admin = adminService.getById(adminId);
            return Result.success(admin);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/create")
    public Result<Admin> create(@RequestBody Admin admin) {
        try {
            Admin created = adminService.create(admin);
            return Result.success(created);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public Result<Admin> update(@PathVariable Long id, @RequestBody Admin admin) {
        try {
            Admin updated = adminService.update(id, admin);
            return Result.success(updated);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
