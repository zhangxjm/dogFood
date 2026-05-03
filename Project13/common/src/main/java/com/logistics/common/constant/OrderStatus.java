package com.logistics.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {
    PENDING(0, "待审核"),
    REVIEWED(1, "已审核"),
    PICKED_UP(2, "已取件"),
    IN_TRANSIT(3, "运输中"),
    DELIVERING(4, "派送中"),
    DELIVERED(5, "已签收"),
    CANCELLED(6, "已取消");

    private final Integer code;
    private final String desc;

    public static String getDesc(Integer code) {
        for (OrderStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status.getDesc();
            }
        }
        return "未知状态";
    }
}
