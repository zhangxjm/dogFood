package com.industry.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DeviceRealtimeData {

    private Long deviceId;
    private String deviceCode;
    private String deviceName;
    private String deviceType;
    private String location;
    private Integer status;
    private BigDecimal temperature;
    private BigDecimal voltage;
    private BigDecimal current;
    private BigDecimal power;
    private Integer runtime;
    private LocalDateTime createTime;
}
