package com.homemaking.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homemaking.common.PageResult;
import com.homemaking.common.Result;
import com.homemaking.entity.*;
import com.homemaking.mapper.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @Autowired
    private SysUserMapper userMapper;
    
    @Autowired
    private WorkerMapper workerMapper;
    
    @Autowired
    private ServiceCategoryMapper categoryMapper;
    
    @Autowired
    private ServiceItemMapper serviceItemMapper;
    
    @Autowired
    private OrdersMapper ordersMapper;
    
    @Autowired
    private ReviewMapper reviewMapper;
    
    @Autowired
    private AnnouncementMapper announcementMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("/users")
    public Result<PageResult<SysUser>> getUsers(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role) {
        
        Page<SysUser> page = new Page<>(current, size);
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(w -> w
                    .like(SysUser::getUsername, keyword)
                    .or()
                    .like(SysUser::getRealName, keyword)
                    .or()
                    .like(SysUser::getPhone, keyword)
            );
        }
        
        if (role != null && !role.isEmpty()) {
            queryWrapper.eq(SysUser::getRole, role);
        }
        
        queryWrapper.orderByDesc(SysUser::getCreateTime);
        
        Page<SysUser> result = userMapper.selectPage(page, queryWrapper);
        result.getRecords().forEach(u -> u.setPassword(null));
        
        PageResult<SysUser> pageResult = new PageResult<>();
        pageResult.setRecords(result.getRecords());
        pageResult.setTotal(result.getTotal());
        pageResult.setSize(result.getSize());
        pageResult.setCurrent(result.getCurrent());
        pageResult.setPages(result.getPages());
        
        return Result.success(pageResult);
    }
    
    @PutMapping("/users/{id}/status")
    public Result<String> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        if ("ADMIN".equals(user.getRole())) {
            return Result.error("不能修改管理员状态");
        }
        user.setStatus(status);
        userMapper.updateById(user);
        return Result.success("状态更新成功");
    }
    
    @GetMapping("/workers")
    public Result<PageResult<Worker>> getWorkers(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String auditStatus) {
        
        List<Worker> workers = workerMapper.selectWorkerList();
        
        if (auditStatus != null && !auditStatus.isEmpty()) {
            workers = workers.stream()
                    .filter(w -> auditStatus.equals(w.getAuditStatus()))
                    .toList();
        }
        
        int start = (current.intValue() - 1) * size.intValue();
        int end = Math.min(start + size.intValue(), workers.size());
        List<Worker> pageRecords = workers.subList(start, end);
        
        PageResult<Worker> pageResult = new PageResult<>();
        pageResult.setRecords(pageRecords);
        pageResult.setTotal((long) workers.size());
        pageResult.setSize(size);
        pageResult.setCurrent(current);
        pageResult.setPages((long) Math.ceil((double) workers.size() / size));
        
        return Result.success(pageResult);
    }
    
    @PutMapping("/workers/{id}/audit")
    @Transactional
    public Result<String> auditWorker(@PathVariable Long id, @RequestBody AuditRequest request) {
        Worker worker = workerMapper.selectById(id);
        if (worker == null) {
            return Result.error("师傅记录不存在");
        }
        
        worker.setAuditStatus(request.getStatus());
        worker.setAuditRemark(request.getRemark());
        workerMapper.updateById(worker);
        
        if ("REJECTED".equals(request.getStatus())) {
            SysUser user = userMapper.selectById(worker.getUserId());
            if (user != null) {
                user.setRole("USER");
                userMapper.updateById(user);
            }
        }
        
        return Result.success("审核完成");
    }
    
    @GetMapping("/categories")
    public Result<List<ServiceCategory>> getCategories() {
        LambdaQueryWrapper<ServiceCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(ServiceCategory::getSort)
                .orderByDesc(ServiceCategory::getCreateTime);
        return Result.success(categoryMapper.selectList(queryWrapper));
    }
    
    @PostMapping("/categories")
    public Result<String> createCategory(@RequestBody ServiceCategory category) {
        category.setStatus(1);
        categoryMapper.insert(category);
        return Result.success("创建成功");
    }
    
    @PutMapping("/categories/{id}")
    public Result<String> updateCategory(@PathVariable Long id, @RequestBody ServiceCategory category) {
        category.setId(id);
        categoryMapper.updateById(category);
        return Result.success("更新成功");
    }
    
    @DeleteMapping("/categories/{id}")
    public Result<String> deleteCategory(@PathVariable Long id) {
        categoryMapper.deleteById(id);
        return Result.success("删除成功");
    }
    
    @GetMapping("/services")
    public Result<PageResult<ServiceItem>> getServices(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        
        Page<ServiceItem> page = new Page<>(current, size);
        LambdaQueryWrapper<ServiceItem> queryWrapper = new LambdaQueryWrapper<>();
        
        if (categoryId != null) {
            queryWrapper.eq(ServiceItem::getCategoryId, categoryId);
        }
        
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.like(ServiceItem::getName, keyword);
        }
        
        queryWrapper.orderByAsc(ServiceItem::getSort)
                .orderByDesc(ServiceItem::getCreateTime);
        
        Page<ServiceItem> result = serviceItemMapper.selectPage(page, queryWrapper);
        
        for (ServiceItem item : result.getRecords()) {
            ServiceCategory category = categoryMapper.selectById(item.getCategoryId());
            if (category != null) {
                item.setCategoryName(category.getName());
            }
        }
        
        PageResult<ServiceItem> pageResult = new PageResult<>();
        pageResult.setRecords(result.getRecords());
        pageResult.setTotal(result.getTotal());
        pageResult.setSize(result.getSize());
        pageResult.setCurrent(result.getCurrent());
        pageResult.setPages(result.getPages());
        
        return Result.success(pageResult);
    }
    
    @PostMapping("/services")
    public Result<String> createService(@RequestBody ServiceItem service) {
        service.setStatus(1);
        serviceItemMapper.insert(service);
        return Result.success("创建成功");
    }
    
    @PutMapping("/services/{id}")
    public Result<String> updateService(@PathVariable Long id, @RequestBody ServiceItem service) {
        service.setId(id);
        serviceItemMapper.updateById(service);
        return Result.success("更新成功");
    }
    
    @DeleteMapping("/services/{id}")
    public Result<String> deleteService(@PathVariable Long id) {
        serviceItemMapper.deleteById(id);
        return Result.success("删除成功");
    }
    
    @GetMapping("/orders")
    public Result<PageResult<Orders>> getOrders(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        
        Page<Orders> page = new Page<>(current, size);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq(Orders::getStatus, status);
        }
        
        queryWrapper.orderByDesc(Orders::getCreateTime);
        
        Page<Orders> result = ordersMapper.selectPage(page, queryWrapper);
        
        for (Orders order : result.getRecords()) {
            SysUser user = userMapper.selectById(order.getUserId());
            if (user != null) {
                order.setUserName(user.getRealName());
                order.setUserPhone(user.getPhone());
            }
            if (order.getWorkerId() != null) {
                LambdaQueryWrapper<Worker> workerQuery = new LambdaQueryWrapper<>();
                workerQuery.eq(Worker::getId, order.getWorkerId());
                Worker worker = workerMapper.selectOne(workerQuery);
                if (worker != null) {
                    SysUser workerUser = userMapper.selectById(worker.getUserId());
                    if (workerUser != null) {
                        order.setWorkerName(workerUser.getRealName());
                        order.setWorkerPhone(workerUser.getPhone());
                    }
                }
            }
        }
        
        PageResult<Orders> pageResult = new PageResult<>();
        pageResult.setRecords(result.getRecords());
        pageResult.setTotal(result.getTotal());
        pageResult.setSize(result.getSize());
        pageResult.setCurrent(result.getCurrent());
        pageResult.setPages(result.getPages());
        
        return Result.success(pageResult);
    }
    
    @GetMapping("/announcements")
    public Result<List<Announcement>> getAnnouncements() {
        LambdaQueryWrapper<Announcement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Announcement::getSort)
                .orderByDesc(Announcement::getCreateTime);
        return Result.success(announcementMapper.selectList(queryWrapper));
    }
    
    @PostMapping("/announcements")
    public Result<String> createAnnouncement(@RequestBody Announcement announcement) {
        announcement.setStatus(1);
        announcementMapper.insert(announcement);
        return Result.success("创建成功");
    }
    
    @PutMapping("/announcements/{id}")
    public Result<String> updateAnnouncement(@PathVariable Long id, @RequestBody Announcement announcement) {
        announcement.setId(id);
        announcementMapper.updateById(announcement);
        return Result.success("更新成功");
    }
    
    @DeleteMapping("/announcements/{id}")
    public Result<String> deleteAnnouncement(@PathVariable Long id) {
        announcementMapper.deleteById(id);
        return Result.success("删除成功");
    }
    
    @Data
    public static class AuditRequest {
        private String status;
        private String remark;
    }
}
