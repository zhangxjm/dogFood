package com.attendance.controller;

import com.attendance.common.PageResult;
import com.attendance.common.Result;
import com.attendance.entity.OvertimeApplication;
import com.attendance.service.OvertimeApplicationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 加班申请管理 Controller
 */
@RestController
@RequestMapping("/api/overtime")
@RequiredArgsConstructor
public class OvertimeApplicationController {

    private final OvertimeApplicationService overtimeApplicationService;

    @GetMapping("/page")
    public Result<PageResult<OvertimeApplication>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long empId,
            @RequestParam(required = false) Integer overtimeType,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<OvertimeApplication> wrapper = new LambdaQueryWrapper<>();
        if (empId != null) {
            wrapper.eq(OvertimeApplication::getEmpId, empId);
        }
        if (overtimeType != null) {
            wrapper.eq(OvertimeApplication::getOvertimeType, overtimeType);
        }
        if (status != null) {
            wrapper.eq(OvertimeApplication::getStatus, status);
        }
        wrapper.orderByDesc(OvertimeApplication::getCreateTime);
        
        Page<OvertimeApplication> page = new Page<>(current, size);
        overtimeApplicationService.page(page, wrapper);
        
        PageResult<OvertimeApplication> result = new PageResult<>(
                page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent());
        return Result.success(result);
    }

    @GetMapping("/list")
    public Result<List<OvertimeApplication>> list(
            @RequestParam(required = false) Long empId,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<OvertimeApplication> wrapper = new LambdaQueryWrapper<>();
        if (empId != null) {
            wrapper.eq(OvertimeApplication::getEmpId, empId);
        }
        if (status != null) {
            wrapper.eq(OvertimeApplication::getStatus, status);
        }
        wrapper.orderByDesc(OvertimeApplication::getCreateTime);
        List<OvertimeApplication> list = overtimeApplicationService.list(wrapper);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<OvertimeApplication> getById(@PathVariable Long id) {
        OvertimeApplication application = overtimeApplicationService.getById(id);
        return Result.success(application);
    }

    @PostMapping
    public Result<OvertimeApplication> save(@RequestBody OvertimeApplication application) {
        if (application.getStartTime() != null && application.getEndTime() != null) {
            long minutes = java.time.Duration.between(application.getStartTime(), application.getEndTime()).toMinutes();
            application.setOvertimeHours(java.math.BigDecimal.valueOf(minutes).divide(java.math.BigDecimal.valueOf(60), 2, java.math.RoundingMode.HALF_UP));
        }
        application.setStatus(0);
        overtimeApplicationService.save(application);
        return Result.success("申请提交成功", application);
    }

    @PutMapping
    public Result<OvertimeApplication> update(@RequestBody OvertimeApplication application) {
        if (application.getStartTime() != null && application.getEndTime() != null) {
            long minutes = java.time.Duration.between(application.getStartTime(), application.getEndTime()).toMinutes();
            application.setOvertimeHours(java.math.BigDecimal.valueOf(minutes).divide(java.math.BigDecimal.valueOf(60), 2, java.math.RoundingMode.HALF_UP));
        }
        overtimeApplicationService.updateById(application);
        return Result.success("修改成功", application);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        overtimeApplicationService.removeById(id);
        return Result.success();
    }

    @PostMapping("/approve")
    public Result<Void> approve(@RequestBody ApproveForm form) {
        OvertimeApplication application = overtimeApplicationService.getById(form.getId());
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
        overtimeApplicationService.updateById(application);
        
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
