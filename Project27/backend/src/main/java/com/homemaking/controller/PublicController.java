package com.homemaking.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homemaking.common.PageResult;
import com.homemaking.common.Result;
import com.homemaking.entity.*;
import com.homemaking.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicController {
    
    @Autowired
    private ServiceCategoryMapper categoryMapper;
    
    @Autowired
    private ServiceItemMapper serviceItemMapper;
    
    @Autowired
    private WorkerMapper workerMapper;
    
    @Autowired
    private AnnouncementMapper announcementMapper;
    
    @Autowired
    private ReviewMapper reviewMapper;
    
    @Autowired
    private SysUserMapper userMapper;
    
    @GetMapping("/categories")
    public Result<List<ServiceCategory>> getCategories() {
        LambdaQueryWrapper<ServiceCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ServiceCategory::getStatus, 1)
                .orderByAsc(ServiceCategory::getSort)
                .orderByDesc(ServiceCategory::getCreateTime);
        return Result.success(categoryMapper.selectList(queryWrapper));
    }
    
    @GetMapping("/services")
    public Result<PageResult<ServiceItem>> getServices(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        
        Page<ServiceItem> page = new Page<>(current, size);
        LambdaQueryWrapper<ServiceItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ServiceItem::getStatus, 1);
        
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
    
    @GetMapping("/services/{id}")
    public Result<ServiceItem> getServiceById(@PathVariable Long id) {
        ServiceItem item = serviceItemMapper.selectById(id);
        if (item == null) {
            return Result.error("服务不存在");
        }
        ServiceCategory category = categoryMapper.selectById(item.getCategoryId());
        if (category != null) {
            item.setCategoryName(category.getName());
        }
        return Result.success(item);
    }
    
    @GetMapping("/workers")
    public Result<List<Worker>> getWorkers(@RequestParam(required = false) Long categoryId) {
        List<Worker> workers = workerMapper.selectWorkerList();
        
        workers = workers.stream()
                .filter(w -> "APPROVED".equals(w.getAuditStatus()))
                .toList();
        
        if (categoryId != null) {
            workers = workers.stream()
                    .filter(w -> categoryId.equals(w.getServiceCategoryId()))
                    .toList();
        }
        
        return Result.success(workers);
    }
    
    @GetMapping("/workers/{id}")
    public Result<Worker> getWorkerById(@PathVariable Long id) {
        List<Worker> workers = workerMapper.selectWorkerList();
        Worker worker = workers.stream()
                .filter(w -> w.getId().equals(id))
                .findFirst()
                .orElse(null);
        
        if (worker == null) {
            return Result.error("师傅不存在");
        }
        return Result.success(worker);
    }
    
    @GetMapping("/announcements")
    public Result<List<Announcement>> getAnnouncements() {
        LambdaQueryWrapper<Announcement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Announcement::getStatus, 1)
                .orderByAsc(Announcement::getSort)
                .orderByDesc(Announcement::getCreateTime);
        return Result.success(announcementMapper.selectList(queryWrapper));
    }
    
    @GetMapping("/reviews/worker/{workerId}")
    public Result<List<Review>> getReviewsByWorker(@PathVariable Long workerId) {
        LambdaQueryWrapper<Review> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Review::getWorkerId, workerId)
                .orderByDesc(Review::getCreateTime);
        
        List<Review> reviews = reviewMapper.selectList(queryWrapper);
        
        for (Review review : reviews) {
            SysUser user = userMapper.selectById(review.getUserId());
            if (user != null) {
                review.setUserName(user.getRealName());
                review.setUserAvatar(user.getAvatar());
            }
        }
        
        return Result.success(reviews);
    }
}
