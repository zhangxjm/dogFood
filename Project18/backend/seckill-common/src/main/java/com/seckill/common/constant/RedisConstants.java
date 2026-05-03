package com.seckill.common.constant;

public class RedisConstants {

    public static final String SECKILL_STOCK_KEY = "seckill:stock:";
    public static final String SECKILL_USER_KEY = "seckill:user:";
    public static final String SECKILL_ACTIVITY_KEY = "seckill:activity:";
    public static final String SECKILL_ORDER_RESULT_KEY = "seckill:order:result:";
    
    public static final String PRODUCT_STOCK_KEY = "product:stock:";
    public static final String PRODUCT_INFO_KEY = "product:info:";
    public static final String PRODUCT_LIST_KEY = "product:list";
    
    public static final String USER_INFO_KEY = "user:info:";
    public static final String USER_TOKEN_KEY = "user:token:";
    
    public static final String LOCK_PREFIX = "lock:";
    public static final String SECKILL_LOCK_KEY = "lock:seckill:";
    public static final String ORDER_LOCK_KEY = "lock:order:";
    
    public static final long STOCK_CACHE_EXPIRE = 30 * 60L;
    public static final long PRODUCT_CACHE_EXPIRE = 10 * 60L;
    public static final long USER_CACHE_EXPIRE = 30 * 60L;
    public static final long TOKEN_EXPIRE = 7 * 24 * 60 * 60L;
    
    public static final String HOT_PRODUCT_KEY = "hot:product:";
    public static final String ACCESS_LIMIT_KEY = "access:limit:";
}
