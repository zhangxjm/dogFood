package com.jobrecruitment.controller;

import com.jobrecruitment.common.Result;
import com.jobrecruitment.dto.PageDTO;
import com.jobrecruitment.mapper.CompanyMapper;
import com.jobrecruitment.mapper.JobApplicationMapper;
import com.jobrecruitment.mapper.JobMapper;
import com.jobrecruitment.mapper.UserMapper;
import com.jobrecruitment.service.CompanyService;
import com.jobrecruitment.service.JobService;
import com.jobrecruitment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final CompanyService companyService;
    private final JobService jobService;
    private final JobApplicationMapper jobApplicationMapper;
    private final JobMapper jobMapper;
    private final UserMapper userMapper;
    private final CompanyMapper companyMapper;

    @GetMapping("/statistics")
    public Result<Map<String, Object>> statistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userMapper.count());
        stats.put("companyCount", companyMapper.count());
        stats.put("jobCount", jobMapper.count());
        stats.put("applicationCount", jobApplicationMapper.count());
        return Result.success(stats);
    }

    @GetMapping("/user/list")
    public Result<Map<String, Object>> userList(PageDTO dto) {
        var users = userService.list(dto);
        var total = userService.count(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("records", users);
        result.put("total", total);
        result.put("pageNum", dto.getPageNum());
        result.put("pageSize", dto.getPageSize());
        return Result.success(result);
    }

    @PutMapping("/user/status/{id}")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return Result.success();
    }

    @GetMapping("/company/list")
    public Result<Map<String, Object>> companyList(PageDTO dto) {
        var companies = companyService.list(dto);
        var total = companyService.count(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("records", companies);
        result.put("total", total);
        result.put("pageNum", dto.getPageNum());
        result.put("pageSize", dto.getPageSize());
        return Result.success(result);
    }

    @PutMapping("/company/audit/{id}")
    public Result<Void> auditCompany(@PathVariable Long id, @RequestParam Integer status) {
        companyService.audit(id, status);
        return Result.success();
    }

    @GetMapping("/job/list")
    public Result<Map<String, Object>> jobList(PageDTO dto) {
        var jobs = jobService.list(dto);
        var total = jobService.count(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("records", jobs);
        result.put("total", total);
        result.put("pageNum", dto.getPageNum());
        result.put("pageSize", dto.getPageSize());
        return Result.success(result);
    }

    @PutMapping("/job/status/{id}")
    public Result<Void> updateJobStatus(@PathVariable Long id, @RequestParam Integer status) {
        jobService.updateStatus(id, status);
        return Result.success();
    }
}
