package com.example.admin.controller;

import com.example.admin.common.Result;
import com.example.admin.entity.LoginUser;
import com.example.admin.entity.SysDept;
import com.example.admin.service.SysDeptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/system/dept")
@RequiredArgsConstructor
public class SysDeptController {

    private final SysDeptService deptService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:dept:list')")
    public Result<List<SysDept>> list(SysDept dept) {
        return deptService.selectDeptList(dept);
    }

    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('system:dept:list')")
    public Result<List<Map<String, Object>>> tree() {
        return deptService.selectDeptTree();
    }

    @GetMapping("/{deptId}")
    @PreAuthorize("hasAuthority('system:dept:query')")
    public Result<SysDept> getInfo(@PathVariable Long deptId) {
        return deptService.selectDeptById(deptId);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:dept:add')")
    public Result<Void> add(@RequestBody SysDept dept, @AuthenticationPrincipal LoginUser loginUser) {
        dept.setCreateBy(loginUser.getUser().getUserName());
        return deptService.insertDept(dept);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('system:dept:edit')")
    public Result<Void> edit(@RequestBody SysDept dept, @AuthenticationPrincipal LoginUser loginUser) {
        dept.setUpdateBy(loginUser.getUser().getUserName());
        return deptService.updateDept(dept);
    }

    @DeleteMapping("/{deptId}")
    @PreAuthorize("hasAuthority('system:dept:remove')")
    public Result<Void> remove(@PathVariable Long deptId) {
        return deptService.deleteDeptById(deptId);
    }
}
