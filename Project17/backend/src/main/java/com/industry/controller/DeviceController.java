package com.industry.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.industry.common.Result;
import com.industry.entity.Device;
import com.industry.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping
    public Result<Page<Device>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String deviceName,
            @RequestParam(required = false) String deviceType,
            @RequestParam(required = false) Integer status) {
        return Result.success(deviceService.page(page, size, deviceName, deviceType, status));
    }

    @GetMapping("/list")
    public Result<List<Device>> listAll() {
        return Result.success(deviceService.listAll());
    }

    @GetMapping("/{id}")
    public Result<Device> getById(@PathVariable Long id) {
        return Result.success(deviceService.getById(id));
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody Device device) {
        return Result.success(deviceService.save(device));
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody Device device) {
        return Result.success(deviceService.update(device));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(deviceService.delete(id));
    }

    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        return Result.success(deviceService.updateStatus(id, status));
    }
}
