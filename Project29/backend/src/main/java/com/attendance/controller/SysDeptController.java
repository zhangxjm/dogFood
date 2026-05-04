package com.attendance.controller;

import com.attendance.common.PageResult;
import com.attendance.common.Result;
import com.attendance.entity.SysDept;
import com.attendance.service.SysDeptService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理 Controller
 */
@RestController
@RequestMapping("/api/dept")
@RequiredArgsConstructor
public class SysDeptController {

    private final SysDeptService sysDeptService;

    @GetMapping("/list")
    public Result<List<SysDept>> list(
            @RequestParam(required = false) String deptName,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(deptName)) {
            wrapper.like(SysDept::getDeptName, deptName);
        }
        if (status != null) {
            wrapper.eq(SysDept::getStatus, status);
        }
        wrapper.orderByAsc(SysDept::getSort);
        List<SysDept> list = sysDeptService.list(wrapper);
        return Result.success(list);
    }

    @GetMapping("/page")
    public Result<PageResult<SysDept>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String deptName,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(deptName)) {
            wrapper.like(SysDept::getDeptName, deptName);
        }
        if (status != null) {
            wrapper.eq(SysDept::getStatus, status);
        }
        wrapper.orderByAsc(SysDept::getSort);
        
        Page<SysDept> page = new Page<>(current, size);
        sysDeptService.page(page, wrapper);
        
        PageResult<SysDept> result = new PageResult<>(
                page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<SysDept> getById(@PathVariable Long id) {
        SysDept dept = sysDeptService.getById(id);
        return Result.success(dept);
    }

    @PostMapping
    public Result<SysDept> save(@RequestBody SysDept dept) {
        sysDeptService.save(dept);
        return Result.success("新增成功", dept);
    }

    @PutMapping
    public Result<SysDept> update(@RequestBody SysDept dept) {
        sysDeptService.updateById(dept);
        return Result.success("修改成功", dept);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysDeptService.removeById(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        sysDeptService.removeByIds(ids);
        return Result.success();
    }
}
