package com.industry.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.industry.dto.WsMessage;
import com.industry.entity.Alarm;
import com.industry.entity.Device;
import com.industry.entity.DeviceData;
import com.industry.mapper.AlarmMapper;
import com.industry.websocket.WebSocketServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmMapper alarmMapper;

    public void checkAndCreateAlarm(Device device, DeviceData data) {
        if (device.getStatus() == 0) {
            return;
        }

        if (data.getTemperature() != null && 
            data.getTemperature().compareTo(device.getTemperatureThreshold()) > 0) {
            createAlarm(device, data, "TEMPERATURE", "温度过高", 
                data.getTemperature().toString(), device.getTemperatureThreshold().toString(), 3);
        }

        if (data.getVoltage() != null) {
            if (data.getVoltage().compareTo(device.getVoltageMin()) < 0) {
                createAlarm(device, data, "VOLTAGE_LOW", "电压过低", 
                    data.getVoltage().toString(), device.getVoltageMin().toString(), 2);
            }
            if (data.getVoltage().compareTo(device.getVoltageMax()) > 0) {
                createAlarm(device, data, "VOLTAGE_HIGH", "电压过高", 
                    data.getVoltage().toString(), device.getVoltageMax().toString(), 2);
            }
        }

        if (data.getStatus() == 2) {
            createAlarm(device, data, "DEVICE_FAULT", "设备故障", 
                "故障状态", "正常运行", 3);
        }
    }

    private void createAlarm(Device device, DeviceData data, String alarmType, 
                            String message, String currentValue, String thresholdValue, int level) {
        LambdaQueryWrapper<Alarm> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Alarm::getDeviceId, device.getId())
               .eq(Alarm::getAlarmType, alarmType)
               .eq(Alarm::getStatus, 0)
               .orderByDesc(Alarm::getCreateTime)
               .last("LIMIT 1");
        
        Alarm existingAlarm = alarmMapper.selectOne(wrapper);
        
        if (existingAlarm != null && 
            existingAlarm.getCreateTime().isAfter(LocalDateTime.now().minusMinutes(1))) {
            return;
        }

        Alarm alarm = new Alarm();
        alarm.setDeviceId(device.getId());
        alarm.setAlarmType(alarmType);
        alarm.setAlarmLevel(level);
        alarm.setAlarmMessage(device.getDeviceName() + ": " + message);
        alarm.setCurrentValue(currentValue);
        alarm.setThresholdValue(thresholdValue);
        alarm.setStatus(0);
        
        alarmMapper.insert(alarm);
        
        log.warn("创建报警: 设备={}, 类型={}, 消息={}", device.getDeviceName(), alarmType, message);
        
        if (WebSocketServer.getOnlineCount() > 0) {
            WebSocketServer.sendToAll(WsMessage.of("ALARM", alarm));
        }
    }
}
