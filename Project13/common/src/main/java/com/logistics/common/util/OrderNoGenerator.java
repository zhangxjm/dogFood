package com.logistics.common.util;

import cn.hutool.core.date.DateUtil;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderNoGenerator {

    private static final AtomicInteger counter = new AtomicInteger(0);
    private static final int MAX_COUNTER = 9999;

    public static String generateOrderNo() {
        String dateStr = DateUtil.format(new Date(), "yyyyMMddHHmmss");
        int current = counter.getAndIncrement();
        if (current > MAX_COUNTER) {
            counter.set(0);
            current = 0;
        }
        String sequence = String.format("%04d", current);
        return "LT" + dateStr + sequence;
    }
}
