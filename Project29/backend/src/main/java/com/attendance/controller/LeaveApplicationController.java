package com.attendance.controller;

import com.attendance.common.PageResult;
import com.attendance.common.Result;
import com.attendance.entity.LeaveApplication;
import com.attendance.service.LeaveApplicationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 请假申请管理 Controller
 */
@RestController
@RequestMapping("/api/leave")
@RequiredArgsConstructor
public class LeaveApplicationController {

    private final LeaveApplicationService leaveApplicationService;

    @GetMapping("/page")
    public Result<PageResult<LeaveApplication>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long empId,
            @RequestParam(required = false) Integer leaveType,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<LeaveApplication> wrapper = new LambdaQueryWrapper<>();
        if (empId != null) {
            wrapper.eq(LeaveApplication::getEmpId, empId);
        }
        if (leaveType != null) {
            wrapper.eq(LeaveApplication::getLeaveType, leaveType);
        }
        if (status != null) {
            wrapper.eq(LeaveApplication::getStatus, status);
        }
        wrapper.orderByDesc(LeaveApplication::getCreateTime);
        
        Page<LeaveApplication> page = new Page<>(current, size);
        leaveApplicationService.page(page, wrapper);
        
        PageResult<LeaveApplication> result = new PageResult<>(
                page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent());
        return Result.success(result);
    }

    @GetMapping("/list")
    public Result<List<LeaveApplication>> list(
            @RequestParam(required = false) Long empId,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<LeaveApplication> wrapper = new LambdaQueryWrapper<>();
        if (empId != null) {
            wrapper.eq(LeaveApplication::getEmpId, empId);
        }
        if (status != null) {
            wrapper.eq(LeaveApplication::getStatus, status);
        }
        wrapper.orderByDesc(LeaveApplication::getCreateTime);
        List<LeaveApplication> list = leaveApplicationService.list(wrapper);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<LeaveApplication> getById(@PathVariable Long id) {
        LeaveApplication application = leaveApplicationService.getById(id);
        return Result.success(application);
    }

    @PostMapping
    public Result<LeaveApplication> save(@RequestBody LeaveApplication application) {
        if (application.getStartTime() != null && application.getEndTime() != null) {
            long minutes = java.time.Duration.between(application.getStartTime(), application.getEndTime()).toMinutes();
            application.setLeaveDays(java.math.BigDecimal.valueOf(minutes).divide(java.math.BigDecimal.valueOf(1440), 1, java.math.RoundingMode.HALF_UP));
        }
        application.setStatus(0);
        leaveApplicationService.save(application);
        return Result.success("申请提交成功", application);
    }

    @PutMapping
    public Result<LeaveApplication> update(@RequestBody LeaveApplication application) {
        if (application.getStartTime() != null && application.getEndTime() != null) {
            long minutes = java.time.Duration.between(application.getStartTime(), application.getEndTime()).toMinutes();
            application.setLeaveDays(java.math.BigDecimal.valueOf(minutes).divide(java.math.BigDecimal.valueOf(1440), 1, java.math.RoundingMode.HALF_UP));
        }
        leaveApplicationService.updateById(application);
        return Result.success("修改成功", application);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        leaveApplicationService.removeById(id);
        return Result.success();
    }

    @PostMapping("/approve")
    public Result<Void> approve(@RequestBody ApproveForm form) {
        LeaveApplication application = leaveApplicationService.getById(form.getId());
        if (application == null) {
            return Result.error("申请不存在");
        }
        if (application.getStatus() != 0) {
            return Result.error("申请已审批");
        }
        
        application.setStatus(form.getStatus());
        application.setApproverId(form.getApproverId());
        application.setApproveTime(LocalDateTime.now());
        application.setApproveRemark(form.getRemark());
        leaveApplicationService.updateById(application);
        
        return Result.success();
    }

    @Data
    public static class ApproveForm {
        private Long id;
        private Integer status;
        private Long approverId;
        private String remark;
    }
}
