package com.industry.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.industry.entity.DeviceData;
import com.industry.mapper.DeviceDataMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceDataService {

    private final DeviceDataMapper deviceDataMapper;

    public List<DeviceData> getRecentData(Long deviceId, int limit) {
        return deviceDataMapper.selectRecentData(deviceId, limit);
    }

    public Page<DeviceData> page(int page, int size, Long deviceId, LocalDateTime startTime, LocalDateTime endTime) {
        Page<DeviceData> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<DeviceData> wrapper = new LambdaQueryWrapper<>();
        
        if (deviceId != null) {
            wrapper.eq(DeviceData::getDeviceId, deviceId);
        }
        if (startTime != null) {
            wrapper.ge(DeviceData::getCreateTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(DeviceData::getCreateTime, endTime);
        }
        
        wrapper.orderByDesc(DeviceData::getCreateTime);
        return deviceDataMapper.selectPage(pageParam, wrapper);
    }

    public List<DeviceData> getLatestData() {
        return deviceDataMapper.selectLatestData();
    }

    public DeviceData getLatestByDeviceId(Long deviceId) {
        return deviceDataMapper.selectOne(
            new LambdaQueryWrapper<DeviceData>()
                .eq(DeviceData::getDeviceId, deviceId)
                .orderByDesc(DeviceData::getCreateTime)
                .last("LIMIT 1")
        );
    }
}
