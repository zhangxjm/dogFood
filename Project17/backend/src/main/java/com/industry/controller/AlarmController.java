package com.industry.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.industry.common.Result;
import com.industry.entity.Alarm;
import com.industry.mapper.AlarmMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/alarms")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmMapper alarmMapper;

    @GetMapping
    public Result<Page<Alarm>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long deviceId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer alarmLevel,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        
        Page<Alarm> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Alarm> wrapper = new LambdaQueryWrapper<>();
        
        if (deviceId != null) {
            wrapper.eq(Alarm::getDeviceId, deviceId);
        }
        if (status != null) {
            wrapper.eq(Alarm::getStatus, status);
        }
        if (alarmLevel != null) {
            wrapper.eq(Alarm::getAlarmLevel, alarmLevel);
        }
        if (startTime != null) {
            wrapper.ge(Alarm::getCreateTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(Alarm::getCreateTime, endTime);
        }
        
        wrapper.orderByDesc(Alarm::getCreateTime);
        return Result.success(alarmMapper.selectPage(pageParam, wrapper));
    }

    @GetMapping("/{id}")
    public Result<Alarm> getById(@PathVariable Long id) {
        return Result.success(alarmMapper.selectById(id));
    }

    @PutMapping("/{id}/handle")
    public Result<Boolean> handle(
            @PathVariable Long id,
            @RequestParam String handleUser,
            @RequestParam(required = false) String handleRemark) {
        Alarm alarm = new Alarm();
        alarm.setId(id);
        alarm.setStatus(1);
        alarm.setHandleTime(LocalDateTime.now());
        alarm.setHandleUser(handleUser);
        alarm.setHandleRemark(handleRemark);
        return Result.success(alarmMapper.updateById(alarm) > 0);
    }

    @GetMapping("/unhandled/count")
    public Result<Long> getUnhandledCount() {
        Long count = alarmMapper.selectCount(
            new LambdaQueryWrapper<Alarm>().eq(Alarm::getStatus, 0)
        );
        return Result.success(count);
    }
}
