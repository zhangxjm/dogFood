package com.jobrecruitment.controller;

import com.jobrecruitment.common.Result;
import com.jobrecruitment.dto.PageDTO;
import com.jobrecruitment.entity.Job;
import com.jobrecruitment.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/job")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(PageDTO dto) {
        var jobs = jobService.list(dto);
        var total = jobService.count(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("records", jobs);
        result.put("total", total);
        result.put("pageNum", dto.getPageNum());
        result.put("pageSize", dto.getPageSize());
        return Result.success(result);
    }

    @GetMapping("/detail/{id}")
    public Result<Job> detail(@PathVariable Long id) {
        jobService.incrementViewCount(id);
        Job job = jobService.getById(id);
        return Result.success(job);
    }

    @GetMapping("/company/list")
    public Result<Map<String, Object>> listByCompany(PageDTO dto) {
        var jobs = jobService.listByCompany(dto);
        var total = jobService.countByCompany(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("records", jobs);
        result.put("total", total);
        result.put("pageNum", dto.getPageNum());
        result.put("pageSize", dto.getPageSize());
        return Result.success(result);
    }

    @PostMapping("/publish")
    public Result<Job> publish(@RequestBody Job job) {
        Job result = jobService.publish(job);
        return Result.success(result);
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody Job job) {
        jobService.update(job);
        return Result.success();
    }

    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        jobService.updateStatus(id, status);
        return Result.success();
    }
}
