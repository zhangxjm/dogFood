package com.industry.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.industry.common.Result;
import com.industry.entity.DeviceData;
import com.industry.service.DeviceDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/device-data")
@RequiredArgsConstructor
public class DeviceDataController {

    private final DeviceDataService deviceDataService;

    @GetMapping
    public Result<Page<DeviceData>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long deviceId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return Result.success(deviceDataService.page(page, size, deviceId, startTime, endTime));
    }

    @GetMapping("/latest")
    public Result<List<DeviceData>> getLatestData() {
        return Result.success(deviceDataService.getLatestData());
    }

    @GetMapping("/device/{deviceId}/recent")
    public Result<List<DeviceData>> getRecentData(
            @PathVariable Long deviceId,
            @RequestParam(defaultValue = "100") int limit) {
        return Result.success(deviceDataService.getRecentData(deviceId, limit));
    }

    @GetMapping("/device/{deviceId}/latest")
    public Result<DeviceData> getLatestByDeviceId(@PathVariable Long deviceId) {
        return Result.success(deviceDataService.getLatestByDeviceId(deviceId));
    }
}
