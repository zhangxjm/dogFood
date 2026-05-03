package com.industry.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.industry.common.Result;
import com.industry.entity.Device;
import com.industry.entity.Alarm;
import com.industry.entity.WorkOrder;
import com.industry.mapper.DeviceMapper;
import com.industry.mapper.AlarmMapper;
import com.industry.mapper.WorkOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DeviceMapper deviceMapper;
    private final AlarmMapper alarmMapper;
    private final WorkOrderMapper workOrderMapper;

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalDevices = deviceMapper.selectCount(
            new LambdaQueryWrapper<Device>().eq(Device::getDeleted, 0)
        );
        
        long onlineDevices = deviceMapper.selectCount(
            new LambdaQueryWrapper<Device>()
                .eq(Device::getDeleted, 0)
                .eq(Device::getStatus, 1)
        );
        
        long offlineDevices = deviceMapper.selectCount(
            new LambdaQueryWrapper<Device>()
                .eq(Device::getDeleted, 0)
                .eq(Device::getStatus, 0)
        );
        
        long faultDevices = deviceMapper.selectCount(
            new LambdaQueryWrapper<Device>()
                .eq(Device::getDeleted, 0)
                .eq(Device::getStatus, 2)
        );
        
        long unhandledAlarms = alarmMapper.selectCount(
            new LambdaQueryWrapper<Alarm>().eq(Alarm::getStatus, 0)
        );
        
        long pendingOrders = workOrderMapper.selectCount(
            new LambdaQueryWrapper<WorkOrder>()
                .eq(WorkOrder::getDeleted, 0)
                .eq(WorkOrder::getStatus, 0)
        );
        
        long processingOrders = workOrderMapper.selectCount(
            new LambdaQueryWrapper<WorkOrder>()
                .eq(WorkOrder::getDeleted, 0)
                .eq(WorkOrder::getStatus, 1)
        );
        
        stats.put("totalDevices", totalDevices);
        stats.put("onlineDevices", onlineDevices);
        stats.put("offlineDevices", offlineDevices);
        stats.put("faultDevices", faultDevices);
        stats.put("unhandledAlarms", unhandledAlarms);
        stats.put("pendingOrders", pendingOrders);
        stats.put("processingOrders", processingOrders);
        
        return Result.success(stats);
    }
}
