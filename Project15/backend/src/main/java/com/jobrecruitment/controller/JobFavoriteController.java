package com.jobrecruitment.controller;

import com.jobrecruitment.common.Result;
import com.jobrecruitment.dto.PageDTO;
import com.jobrecruitment.service.JobFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/favorite")
@RequiredArgsConstructor
public class JobFavoriteController {

    private final JobFavoriteService jobFavoriteService;

    @PostMapping("/toggle/{jobId}")
    public Result<Void> toggle(@PathVariable Long jobId) {
        jobFavoriteService.toggle(jobId);
        return Result.success();
    }

    @GetMapping("/check/{jobId}")
    public Result<Boolean> check(@PathVariable Long jobId) {
        boolean favorited = jobFavoriteService.isFavorited(jobId);
        return Result.success(favorited);
    }

    @GetMapping("/list")
    public Result<Map<String, Object>> list(PageDTO dto) {
        var favorites = jobFavoriteService.list(dto);
        var total = jobFavoriteService.count(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("records", favorites);
        result.put("total", total);
        result.put("pageNum", dto.getPageNum());
        result.put("pageSize", dto.getPageSize());
        return Result.success(result);
    }
}
