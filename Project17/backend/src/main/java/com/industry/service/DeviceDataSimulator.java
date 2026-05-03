package com.industry.service;

import com.industry.entity.Device;
import com.industry.entity.DeviceData;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DeviceDataSimulator {

    private final Random random = new Random();
    private final ConcurrentHashMap<Long, DeviceData> lastDataMap = new ConcurrentHashMap<>();

    public DeviceData generateData(Device device) {
        DeviceData lastData = lastDataMap.get(device.getId());
        
        DeviceData data = new DeviceData();
        data.setDeviceId(device.getId());
        data.setStatus(device.getStatus());
        
        if (device.getStatus() == 0) {
            data.setTemperature(BigDecimal.ZERO);
            data.setVoltage(BigDecimal.ZERO);
            data.setCurrent(BigDecimal.ZERO);
            data.setPower(BigDecimal.ZERO);
            data.setRuntime(0);
            return data;
        }
        
        if (device.getStatus() == 2) {
            data.setTemperature(generateRandomDecimal(90.0, 120.0));
            data.setVoltage(generateRandomDecimal(150.0, 180.0));
            data.setCurrent(generateRandomDecimal(20.0, 30.0));
            data.setPower(data.getVoltage().multiply(data.getCurrent()));
            data.setRuntime(lastData != null ? lastData.getRuntime() + 1 : 0);
            return data;
        }
        
        BigDecimal baseTemp = device.getTemperatureThreshold().multiply(new BigDecimal("0.7"));
        BigDecimal tempVariation = generateRandomDecimal(-5.0, 10.0);
        if (lastData != null) {
            BigDecimal change = tempVariation.multiply(new BigDecimal("0.3"));
            data.setTemperature(lastData.getTemperature().add(change).setScale(2, RoundingMode.HALF_UP));
        } else {
            data.setTemperature(baseTemp.add(tempVariation).setScale(2, RoundingMode.HALF_UP));
        }
        
        BigDecimal baseVoltage = new BigDecimal("220.0");
        BigDecimal voltageVariation = generateRandomDecimal(-10.0, 10.0);
        if (lastData != null) {
            BigDecimal change = voltageVariation.multiply(new BigDecimal("0.2"));
            data.setVoltage(lastData.getVoltage().add(change).setScale(2, RoundingMode.HALF_UP));
        } else {
            data.setVoltage(baseVoltage.add(voltageVariation).setScale(2, RoundingMode.HALF_UP));
        }
        
        BigDecimal baseCurrent = generateRandomDecimal(5.0, 15.0);
        if (lastData != null) {
            BigDecimal currentChange = generateRandomDecimal(-2.0, 2.0);
            data.setCurrent(lastData.getCurrent().add(currentChange).setScale(2, RoundingMode.HALF_UP));
        } else {
            data.setCurrent(baseCurrent);
        }
        
        data.setPower(data.getVoltage().multiply(data.getCurrent()).setScale(2, RoundingMode.HALF_UP));
        
        data.setRuntime(lastData != null ? lastData.getRuntime() + 1 : random.nextInt(1000));
        
        if (random.nextDouble() < 0.05) {
            if (random.nextBoolean()) {
                data.setTemperature(data.getTemperature().add(new BigDecimal("20.0")));
            } else {
                data.setVoltage(data.getVoltage().subtract(new BigDecimal("30.0")));
            }
        }
        
        lastDataMap.put(device.getId(), data);
        
        return data;
    }

    private BigDecimal generateRandomDecimal(double min, double max) {
        double value = min + (max - min) * random.nextDouble();
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }

    public void resetDeviceData(Long deviceId) {
        lastDataMap.remove(deviceId);
    }
}
