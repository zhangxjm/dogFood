package com.industry.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.industry.dto.DeviceRealtimeData;
import com.industry.dto.WsMessage;
import com.industry.entity.Device;
import com.industry.entity.DeviceData;
import com.industry.mapper.DeviceDataMapper;
import com.industry.mapper.DeviceMapper;
import com.industry.websocket.WebSocketServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceDataScheduler {

    private final DeviceMapper deviceMapper;
    private final DeviceDataMapper deviceDataMapper;
    private final DeviceDataSimulator dataSimulator;
    private final AlarmService alarmService;

    @Scheduled(fixedRate = 2000)
    public void generateAndPushDeviceData() {
        try {
            List<Device> devices = deviceMapper.selectList(
                new LambdaQueryWrapper<Device>().eq(Device::getDeleted, 0)
            );

            List<DeviceRealtimeData> realtimeDataList = new ArrayList<>();

            for (Device device : devices) {
                DeviceData data = dataSimulator.generateData(device);
                deviceDataMapper.insert(data);

                DeviceRealtimeData realtimeData = convertToRealtimeData(device, data);
                realtimeDataList.add(realtimeData);

                alarmService.checkAndCreateAlarm(device, data);
            }

            if (!realtimeDataList.isEmpty() && WebSocketServer.getOnlineCount() > 0) {
                WebSocketServer.sendToAll(WsMessage.of("DEVICE_DATA", realtimeDataList));
            }

        } catch (Exception e) {
            log.error("生成设备数据失败: {}", e.getMessage(), e);
        }
    }

    private DeviceRealtimeData convertToRealtimeData(Device device, DeviceData data) {
        DeviceRealtimeData realtimeData = new DeviceRealtimeData();
        realtimeData.setDeviceId(device.getId());
        realtimeData.setDeviceCode(device.getDeviceCode());
        realtimeData.setDeviceName(device.getDeviceName());
        realtimeData.setDeviceType(device.getDeviceType());
        realtimeData.setLocation(device.getLocation());
        realtimeData.setStatus(data.getStatus());
        realtimeData.setTemperature(data.getTemperature());
        realtimeData.setVoltage(data.getVoltage());
        realtimeData.setCurrent(data.getCurrent());
        realtimeData.setPower(data.getPower());
        realtimeData.setRuntime(data.getRuntime());
        realtimeData.setCreateTime(data.getCreateTime());
        return realtimeData;
    }
}
