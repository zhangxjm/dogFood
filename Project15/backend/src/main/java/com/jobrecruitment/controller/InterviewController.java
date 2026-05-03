package com.jobrecruitment.controller;

import com.jobrecruitment.common.Result;
import com.jobrecruitment.dto.PageDTO;
import com.jobrecruitment.entity.Interview;
import com.jobrecruitment.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/interview")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping("/create")
    public Result<Interview> create(@RequestBody Interview interview) {
        Interview result = interviewService.create(interview);
        return Result.success(result);
    }

    @GetMapping("/company/list")
    public Result<Map<String, Object>> listByCompany(PageDTO dto) {
        var interviews = interviewService.listByCompany(dto);
        var total = interviewService.countByCompany(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("records", interviews);
        result.put("total", total);
        result.put("pageNum", dto.getPageNum());
        result.put("pageSize", dto.getPageSize());
        return Result.success(result);
    }

    @GetMapping("/my/list")
    public Result<Map<String, Object>> listByUser(PageDTO dto) {
        var interviews = interviewService.listByUser(dto);
        var total = interviewService.countByUser(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("records", interviews);
        result.put("total", total);
        result.put("pageNum", dto.getPageNum());
        result.put("pageSize", dto.getPageSize());
        return Result.success(result);
    }

    @GetMapping("/detail/{id}")
    public Result<Interview> detail(@PathVariable Long id) {
        Interview interview = interviewService.getById(id);
        return Result.success(interview);
    }

    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        interviewService.updateStatus(id, status);
        return Result.success();
    }
}
