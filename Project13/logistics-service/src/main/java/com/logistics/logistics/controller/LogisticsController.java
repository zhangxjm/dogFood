package com.logistics.logistics.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.logistics.common.dto.LogisticsUpdateDTO;
import com.logistics.common.entity.Logistics;
import com.logistics.common.entity.LogisticsTrack;
import com.logistics.common.result.Result;
import com.logistics.logistics.service.LogisticsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logistics")
@RequiredArgsConstructor
public class LogisticsController {

    private final LogisticsService logisticsService;

    @PostMapping("/init/{orderId}")
    public Result<Logistics> initLogistics(@PathVariable("orderId") Long orderId) {
        Logistics logistics = logisticsService.initLogistics(orderId);
        return Result.success("物流初始化成功", logistics);
    }

    @PostMapping("/update")
    public Result<Void> updateLogistics(@Valid @RequestBody LogisticsUpdateDTO dto) {
        logisticsService.updateLogistics(dto);
        return Result.success("物流状态更新成功", null);
    }

    @GetMapping("/{id}")
    public Result<Logistics> getById(@PathVariable("id") Long id) {
        Logistics logistics = logisticsService.getById(id);
        return Result.success(logistics);
    }

    @GetMapping("/order/{orderId}")
    public Result<Logistics> getByOrderId(@PathVariable("orderId") Long orderId) {
        Logistics logistics = logisticsService.getByOrderId(orderId);
        return Result.success(logistics);
    }

    @GetMapping("/track/{orderId}")
    public Result<List<LogisticsTrack>> getTracksByOrderId(@PathVariable("orderId") Long orderId) {
        List<LogisticsTrack> tracks = logisticsService.getTracksByOrderId(orderId);
        return Result.success(tracks);
    }

    @GetMapping("/page")
    public Result<Page<Logistics>> pageList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        Page<Logistics> page = logisticsService.pageList(current, size, status, keyword);
        return Result.success(page);
    }

    @PostMapping("/save")
    public Result<Void> saveOrUpdate(@RequestBody Logistics logistics) {
        logisticsService.saveOrUpdate(logistics);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        logisticsService.removeById(id);
        return Result.success();
    }
}
