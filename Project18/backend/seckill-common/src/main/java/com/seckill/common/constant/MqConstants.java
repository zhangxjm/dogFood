package com.seckill.common.constant;

public class MqConstants {

    public static final String SECKILL_ORDER_TOPIC = "SECKILL_ORDER_TOPIC";
    public static final String SECKILL_ORDER_GROUP = "SECKILL_ORDER_GROUP";
    
    public static final String ORDER_TIMEOUT_TOPIC = "ORDER_TIMEOUT_TOPIC";
    public static final String ORDER_TIMEOUT_GROUP = "ORDER_TIMEOUT_GROUP";
    
    public static final String STOCK_DEDUCTION_TOPIC = "STOCK_DEDUCTION_TOPIC";
    public static final String STOCK_DEDUCTION_GROUP = "STOCK_DEDUCTION_GROUP";
    
    public static final int DELAY_LEVEL_1M = 4;
    public static final int DELAY_LEVEL_5M = 5;
    public static final int DELAY_LEVEL_10M = 6;
    public static final int DELAY_LEVEL_30M = 7;
    public static final int DELAY_LEVEL_1H = 8;
}
