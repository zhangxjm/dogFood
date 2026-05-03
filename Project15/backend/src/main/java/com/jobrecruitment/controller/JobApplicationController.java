package com.jobrecruitment.controller;

import com.jobrecruitment.common.Result;
import com.jobrecruitment.dto.PageDTO;
import com.jobrecruitment.service.JobApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    @PostMapping("/apply/{jobId}")
    public Result<Void> apply(@PathVariable Long jobId) {
        jobApplicationService.apply(jobId);
        return Result.success();
    }

    @GetMapping("/my/list")
    public Result<Map<String, Object>> myList(PageDTO dto) {
        var applications = jobApplicationService.listMyApplications(dto);
        var total = jobApplicationService.countMyApplications(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("records", applications);
        result.put("total", total);
        result.put("pageNum", dto.getPageNum());
        result.put("pageSize", dto.getPageSize());
        return Result.success(result);
    }

    @GetMapping("/received/list")
    public Result<Map<String, Object>> receivedList(PageDTO dto) {
        var applications = jobApplicationService.listReceivedApplications(dto);
        var total = jobApplicationService.countReceivedApplications(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("records", applications);
        result.put("total", total);
        result.put("pageNum", dto.getPageNum());
        result.put("pageSize", dto.getPageSize());
        return Result.success(result);
    }

    @GetMapping("/detail/{id}")
    public Result<?> detail(@PathVariable Long id) {
        var application = jobApplicationService.getById(id);
        return Result.success(application);
    }

    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        jobApplicationService.updateStatus(id, status);
        return Result.success();
    }
}
