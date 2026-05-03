package com.seckill.common.aspect;

import com.seckill.common.annotation.PreventDuplicateSubmit;
import com.seckill.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
public class PreventDuplicateSubmitAspect {

    @Autowired
    private RedissonClient redissonClient;

    private static final String DUPLICATE_SUBMIT_PREFIX = "duplicate:submit:";

    @Around("@annotation(preventDuplicateSubmit)")
    public Object around(ProceedingJoinPoint point, PreventDuplicateSubmit preventDuplicateSubmit) throws Throwable {
        String key = generateKey(point);
        
        RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
        
        boolean success = atomicLong.compareAndSet(0, 1);
        if (!success) {
            log.warn("重复提交拦截: key={}", key);
            throw BusinessException.of(preventDuplicateSubmit.message());
        }
        
        atomicLong.expire(preventDuplicateSubmit.interval(), TimeUnit.SECONDS);
        
        try {
            return point.proceed();
        } finally {
            atomicLong.delete();
        }
    }

    private String generateKey(ProceedingJoinPoint point) {
        StringBuilder key = new StringBuilder(DUPLICATE_SUBMIT_PREFIX);
        
        String userId = getCurrentUserId();
        if (userId != null) {
            key.append(userId).append(":");
        } else {
            key.append(getClientIp()).append(":");
        }
        
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        key.append(method.getDeclaringClass().getName())
           .append(".")
           .append(method.getName());
        
        return key.toString();
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
            ip = request.getRemoteAddr();
        }
        
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }
}
