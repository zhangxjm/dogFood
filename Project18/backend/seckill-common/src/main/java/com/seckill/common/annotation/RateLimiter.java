package com.seckill.common.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {

    String key() default "";

    int limit() default 10;

    long windowTime() default 1;

    TimeUnit windowTimeUnit() default TimeUnit.SECONDS;

    LimitType limitType() default LimitType.DEFAULT;

    String message() default "请求过于频繁，请稍后再试";

    enum LimitType {
        DEFAULT,
        IP,
        USER
    }
}
