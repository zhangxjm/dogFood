package com.seckill.seckill.controller;

import com.seckill.common.annotation.PreventDuplicateSubmit;
import com.seckill.common.annotation.RateLimiter;
import com.seckill.common.entity.SeckillActivity;
import com.seckill.common.result.PageResult;
import com.seckill.common.result.Result;
import com.seckill.seckill.dto.SeckillExecuteDTO;
import com.seckill.seckill.service.SeckillActivityService;
import com.seckill.common.vo.SeckillResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "秒杀接口", description = "秒杀活动、秒杀下单等接口")
@RestController
@RequestMapping("/seckill")
@RequiredArgsConstructor
public class SeckillController {

    private final SeckillActivityService seckillActivityService;

    @Operation(summary = "获取秒杀活动列表")
    @RateLimiter(limit = 100, windowTime = 1)
    @GetMapping("/list")
    public Result<PageResult<SeckillActivity>> getSeckillList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long productId) {
        PageResult<SeckillActivity> result = seckillActivityService.getActivityList(pageNum, pageSize, status, productId);
        return Result.success(result);
    }

    @Operation(summary = "获取秒杀活动详情")
    @RateLimiter(limit = 100, windowTime = 1)
    @GetMapping("/{id}")
    public Result<SeckillActivity> getSeckillDetail(@PathVariable Long id) {
        SeckillActivity activity = seckillActivityService.getActivityDetail(id);
        return Result.success(activity);
    }

    @Operation(summary = "获取秒杀库存")
    @RateLimiter(limit = 200, windowTime = 1)
    @GetMapping("/stock/{id}")
    public Result<Integer> getSeckillStock(@PathVariable Long id) {
        SeckillActivity activity = seckillActivityService.getActivityDetail(id);
        return Result.success(activity.getStock());
    }

    @Operation(summary = "执行秒杀")
    @PreventDuplicateSubmit(interval = 10)
    @RateLimiter(limit = 50, windowTime = 1, message = "秒杀请求过于频繁，请稍后再试")
    @PostMapping("/execute")
    public Result<SeckillResultVO> executeSeckill(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody SeckillExecuteDTO dto) {
        SeckillResultVO result = seckillActivityService.executeSeckill(userId, dto);
        return Result.success(result);
    }

    @Operation(summary = "获取秒杀结果")
    @GetMapping("/result/{activityId}")
    public Result<SeckillResultVO> getSeckillResult(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long activityId) {
        SeckillResultVO result = seckillActivityService.getSeckillResult(userId, activityId);
        return Result.success(result);
    }
}
