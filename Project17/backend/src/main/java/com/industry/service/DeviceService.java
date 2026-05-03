package com.industry.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.industry.entity.Device;
import com.industry.mapper.DeviceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceMapper deviceMapper;

    public List<Device> listAll() {
        return deviceMapper.selectList(
            new LambdaQueryWrapper<Device>().eq(Device::getDeleted, 0)
        );
    }

    public Page<Device> page(int page, int size, String deviceName, String deviceType, Integer status) {
        Page<Device> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Device::getDeleted, 0);
        
        if (StringUtils.hasText(deviceName)) {
            wrapper.like(Device::getDeviceName, deviceName);
        }
        if (StringUtils.hasText(deviceType)) {
            wrapper.eq(Device::getDeviceType, deviceType);
        }
        if (status != null) {
            wrapper.eq(Device::getStatus, status);
        }
        
        wrapper.orderByDesc(Device::getCreateTime);
        return deviceMapper.selectPage(pageParam, wrapper);
    }

    public Device getById(Long id) {
        return deviceMapper.selectById(id);
    }

    public boolean save(Device device) {
        return deviceMapper.insert(device) > 0;
    }

    public boolean update(Device device) {
        return deviceMapper.updateById(device) > 0;
    }

    public boolean delete(Long id) {
        Device device = new Device();
        device.setId(id);
        device.setDeleted(1);
        return deviceMapper.updateById(device) > 0;
    }

    public boolean updateStatus(Long id, Integer status) {
        Device device = new Device();
        device.setId(id);
        device.setStatus(status);
        return deviceMapper.updateById(device) > 0;
    }
}
