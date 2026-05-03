package com.industry.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class WsMessage {

    private String type;
    private Object data;
    private LocalDateTime timestamp;

    public static WsMessage of(String type, Object data) {
        return WsMessage.builder()
                .type(type)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
