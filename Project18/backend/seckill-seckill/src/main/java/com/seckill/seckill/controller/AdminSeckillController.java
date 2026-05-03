package com.seckill.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.seckill.common.constant.RedisConstants;
import com.seckill.common.entity.SeckillActivity;
import com.seckill.common.exception.BusinessException;
import com.seckill.common.result.PageResult;
import com.seckill.common.result.Result;
import com.seckill.seckill.service.SeckillActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

@Tag(name = "秒杀管理接口", description = "管理后台秒杀活动管理接口")
@RestController
@RequestMapping("/admin/seckill")
@RequiredArgsConstructor
public class AdminSeckillController {

    private final SeckillActivityService seckillActivityService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Operation(summary = "获取秒杀活动分页列表")
    @GetMapping("/page")
    public Result<PageResult<SeckillActivity>> getSeckillPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long productId) {
        PageResult<SeckillActivity> result = seckillActivityService.getActivityList(pageNum, pageSize, status, productId);
        return Result.success(result);
    }

    @Operation(summary = "新增秒杀活动")
    @PostMapping
    public Result<Long> addSeckill(@RequestBody SeckillActivity activity) {
        activity.setStatus(0);
        activity.setStock(activity.getSeckillCount());
        activity.setSales(0);
        seckillActivityService.save(activity);
        return Result.success(activity.getId());
    }

    @Operation(summary = "更新秒杀活动")
    @PutMapping
    public Result<Boolean> updateSeckill(@RequestBody SeckillActivity activity) {
        if (activity.getId() == null) {
            throw BusinessException.of("活动ID不能为空");
        }
        
        boolean success = seckillActivityService.updateById(activity);
        
        String cacheKey = RedisConstants.SECKILL_ACTIVITY_KEY + activity.getId();
        redisTemplate.delete(cacheKey);
        
        return Result.success(success);
    }

    @Operation(summary = "删除秒杀活动")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteSeckill(@PathVariable Long id) {
        seckillActivityService.removeById(id);
        
        String cacheKey = RedisConstants.SECKILL_ACTIVITY_KEY + id;
        redisTemplate.delete(cacheKey);
        
        return Result.success(true);
    }

    @Operation(summary = "开始秒杀活动")
    @PostMapping("/start/{id}")
    public Result<Boolean> startSeckill(@PathVariable Long id) {
        SeckillActivity activity = seckillActivityService.getById(id);
        if (activity == null) {
            throw BusinessException.of("活动不存在");
        }
        
        LambdaUpdateWrapper<SeckillActivity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SeckillActivity::getId, id)
               .set(SeckillActivity::getStatus, 1);
        
        seckillActivityService.update(wrapper);
        seckillActivityService.initSeckillStock(id);
        
        return Result.success(true);
    }

    @Operation(summary = "停止秒杀活动")
    @PostMapping("/stop/{id}")
    public Result<Boolean> stopSeckill(@PathVariable Long id) {
        LambdaUpdateWrapper<SeckillActivity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SeckillActivity::getId, id)
               .set(SeckillActivity::getStatus, 3);
        
        seckillActivityService.update(wrapper);
        
        String stockKey = RedisConstants.SECKILL_STOCK_KEY + id;
        redisTemplate.delete(stockKey);
        
        return Result.success(true);
    }

    @Scheduled(cron = "0 * * * * ?")
    public void updateActivityStatusScheduler() {
        seckillActivityService.updateActivityStatus();
    }
}
