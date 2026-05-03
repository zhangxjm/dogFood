package com.campustrade.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campustrade.common.Result;
import com.campustrade.entity.Product;
import com.campustrade.entity.User;
import com.campustrade.service.ProductService;
import com.campustrade.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProductService productService;
    private final UserService userService;

    private boolean isAdmin(HttpServletRequest request) {
        String userRole = (String) request.getAttribute("userRole");
        return "admin".equals(userRole);
    }

    @GetMapping("/pending-products")
    public Result<Page<Product>> getPendingProducts(
            HttpServletRequest request,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize) {
        
        if (!isAdmin(request)) {
            return Result.error(403, "无权限访问");
        }
        
        Page<Product> page = productService.getPendingProducts(pageNum, pageSize);
        return Result.success(page);
    }

    @PostMapping("/audit-product/{id}")
    public Result<Void> auditProduct(HttpServletRequest request,
                                       @PathVariable Long id,
                                       @RequestBody Map<String, String> params) {
        
        if (!isAdmin(request)) {
            return Result.error(403, "无权限访问");
        }
        
        String status = params.get("status");
        String remark = params.get("remark");
        
        if (!StringUtils.hasText(status)) {
            return Result.error("状态不能为空");
        }
        
        boolean success = productService.auditProduct(id, status, remark);
        if (success) {
            return Result.success("审核成功");
        }
        return Result.error("审核失败");
    }

    @GetMapping("/users")
    public Result<List<User>> getAllUsers(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.error(403, "无权限访问");
        }
        
        List<User> users = userService.list();
        users.forEach(user -> user.setPassword(null));
        return Result.success(users);
    }

    @PostMapping("/update-user-status/{id}")
    public Result<Void> updateUserStatus(HttpServletRequest request,
                                           @PathVariable Long id,
                                           @RequestBody Map<String, Integer> params) {
        
        if (!isAdmin(request)) {
            return Result.error(403, "无权限访问");
        }
        
        Integer status = params.get("status");
        if (status == null) {
            return Result.error("状态不能为空");
        }
        
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        if ("admin".equals(user.getRole())) {
            return Result.error("不能修改管理员状态");
        }
        
        user.setStatus(status);
        boolean success = userService.updateById(user);
        
        if (success) {
            return Result.success("状态更新成功");
        }
        return Result.error("更新失败");
    }

    @DeleteMapping("/delete-user/{id}")
    public Result<Void> deleteUser(HttpServletRequest request, @PathVariable Long id) {
        if (!isAdmin(request)) {
            return Result.error(403, "无权限访问");
        }
        
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        if ("admin".equals(user.getRole())) {
            return Result.error("不能删除管理员");
        }
        
        boolean success = userService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }
}
