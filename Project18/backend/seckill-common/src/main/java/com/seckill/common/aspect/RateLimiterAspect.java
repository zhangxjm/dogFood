package com.seckill.common.aspect;

import com.seckill.common.annotation.RateLimiter;
import com.seckill.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Component
public class RateLimiterAspect {

    @Autowired
    private RedissonClient redissonClient;

    private static final String RATE_LIMITER_PREFIX = "rate:limiter:";

    @Around("@annotation(rateLimiter)")
    public Object around(ProceedingJoinPoint point, RateLimiter rateLimiter) throws Throwable {
        String key = generateKey(point, rateLimiter);
        
        RRateLimiter rRateLimiter = redissonClient.getRateLimiter(key);
        
        if (!rRateLimiter.isExists()) {
            RateIntervalUnit unit = convertToRateIntervalUnit(rateLimiter.windowTimeUnit());
            rRateLimiter.trySetRate(RateType.OVERALL, rateLimiter.limit(), rateLimiter.windowTime(), unit);
        }
        
        boolean acquire = rRateLimiter.tryAcquire();
        if (!acquire) {
            log.warn("限流触发: key={}, limit={}", key, rateLimiter.limit());
            throw BusinessException.of(429, rateLimiter.message());
        }
        
        return point.proceed();
    }

    private String generateKey(ProceedingJoinPoint point, RateLimiter rateLimiter) {
        StringBuilder key = new StringBuilder(RATE_LIMITER_PREFIX);
        
        if (!rateLimiter.key().isEmpty()) {
            key.append(rateLimiter.key());
        } else {
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
            key.append(method.getDeclaringClass().getName())
               .append(".")
               .append(method.getName());
        }
        
        switch (rateLimiter.limitType()) {
            case IP:
                key.append(":").append(getClientIp());
                break;
            case USER:
                String userId = getCurrentUserId();
                if (userId != null) {
                    key.append(":").append(userId);
                }
                break;
            default:
                break;
        }
        
        return key.toString();
    }

    private String getClientIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return "unknown";
        }
        HttpServletRequest request = attributes.getRequest();
        
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }

    private String getCurrentUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        Object userId = request.getAttribute("userId");
        return userId != null ? userId.toString() : null;
    }

    private RateIntervalUnit convertToRateIntervalUnit(java.util.concurrent.TimeUnit timeUnit) {
        return switch (timeUnit) {
            case MILLISECONDS -> RateIntervalUnit.MILLISECONDS;
            case SECONDS -> RateIntervalUnit.SECONDS;
            case MINUTES -> RateIntervalUnit.MINUTES;
            case HOURS -> RateIntervalUnit.HOURS;
            case DAYS -> RateIntervalUnit.DAYS;
            default -> RateIntervalUnit.SECONDS;
        };
    }
}
