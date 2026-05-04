package com.attendance.controller;

import com.attendance.common.PageResult;
import com.attendance.common.Result;
import com.attendance.entity.EmpEmployee;
import com.attendance.service.EmpEmployeeService;
import com.attendance.util.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 员工档案管理 Controller
 */
@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmpEmployeeController {

    private final EmpEmployeeService empEmployeeService;

    @GetMapping("/list")
    public Result<List<EmpEmployee>> list(
            @RequestParam(required = false) String empNo,
            @RequestParam(required = false) String empName,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Long postId,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<EmpEmployee> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(empNo)) {
            wrapper.like(EmpEmployee::getEmpNo, empNo);
        }
        if (StringUtils.hasText(empName)) {
            wrapper.like(EmpEmployee::getEmpName, empName);
        }
        if (deptId != null) {
            wrapper.eq(EmpEmployee::getDeptId, deptId);
        }
        if (postId != null) {
            wrapper.eq(EmpEmployee::getPostId, postId);
        }
        if (status != null) {
            wrapper.eq(EmpEmployee::getStatus, status);
        }
        wrapper.orderByDesc(EmpEmployee::getCreateTime);
        List<EmpEmployee> list = empEmployeeService.list(wrapper);
        return Result.success(list);
    }

    @GetMapping("/page")
    public Result<PageResult<EmpEmployee>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String empNo,
            @RequestParam(required = false) String empName,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Long postId,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<EmpEmployee> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(empNo)) {
            wrapper.like(EmpEmployee::getEmpNo, empNo);
        }
        if (StringUtils.hasText(empName)) {
            wrapper.like(EmpEmployee::getEmpName, empName);
        }
        if (deptId != null) {
            wrapper.eq(EmpEmployee::getDeptId, deptId);
        }
        if (postId != null) {
            wrapper.eq(EmpEmployee::getPostId, postId);
        }
        if (status != null) {
            wrapper.eq(EmpEmployee::getStatus, status);
        }
        wrapper.orderByDesc(EmpEmployee::getCreateTime);
        
        Page<EmpEmployee> page = new Page<>(current, size);
        empEmployeeService.page(page, wrapper);
        
        PageResult<EmpEmployee> result = new PageResult<>(
                page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<EmpEmployee> getById(@PathVariable Long id) {
        EmpEmployee employee = empEmployeeService.getById(id);
        return Result.success(employee);
    }

    @PostMapping
    public Result<EmpEmployee> save(@RequestBody EmpEmployee employee) {
        empEmployeeService.save(employee);
        return Result.success("新增成功", employee);
    }

    @PutMapping
    public Result<EmpEmployee> update(@RequestBody EmpEmployee employee) {
        empEmployeeService.updateById(employee);
        return Result.success("修改成功", employee);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        empEmployeeService.removeById(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        empEmployeeService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping("/export")
    public void export(
            HttpServletResponse response,
            @RequestParam(required = false) String empNo,
            @RequestParam(required = false) String empName,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Long postId,
            @RequestParam(required = false) Integer status) throws IOException {
        LambdaQueryWrapper<EmpEmployee> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(empNo)) {
            wrapper.like(EmpEmployee::getEmpNo, empNo);
        }
        if (StringUtils.hasText(empName)) {
            wrapper.like(EmpEmployee::getEmpName, empName);
        }
        if (deptId != null) {
            wrapper.eq(EmpEmployee::getDeptId, deptId);
        }
        if (postId != null) {
            wrapper.eq(EmpEmployee::getPostId, postId);
        }
        if (status != null) {
            wrapper.eq(EmpEmployee::getStatus, status);
        }
        List<EmpEmployee> list = empEmployeeService.list(wrapper);

        String[] headers = {"工号", "姓名", "性别", "手机号", "邮箱", "入职日期", "状态"};
        
        ExcelUtil.export(response, "员工档案列表", "员工数据", headers, list, emp -> {
            String gender = emp.getGender() != null && emp.getGender() == 1 ? "男" : "女";
            String empStatus = emp.getStatus() != null && emp.getStatus() == 1 ? "在职" : "离职";
            return Arrays.asList(
                    emp.getEmpNo(),
                    emp.getEmpName(),
                    gender,
                    emp.getPhone(),
                    emp.getEmail(),
                    emp.getEntryDate(),
                    empStatus
            );
        });
    }
}
