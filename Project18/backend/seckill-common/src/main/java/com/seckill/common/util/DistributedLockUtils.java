package com.seckill.common.util;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Component
public class DistributedLockUtils {

    private static RedissonClient redissonClient;

    @Autowired
    public void setRedissonClient(RedissonClient redissonClient) {
        DistributedLockUtils.redissonClient = redissonClient;
    }

    private static final String LOCK_PREFIX = "lock:";

    public static String getLockKey(String key) {
        return LOCK_PREFIX + key;
    }

    public static RLock getLock(String key) {
        return redissonClient.getLock(getLockKey(key));
    }

    public static boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit) {
        RLock lock = getLock(key);
        try {
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            log.error("获取锁异常: {}", key, e);
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public static boolean tryLock(String key, long waitTime, TimeUnit unit) {
        return tryLock(key, waitTime, 30, TimeUnit.SECONDS);
    }

    public static boolean tryLock(String key) {
        return tryLock(key, 0, 30, TimeUnit.SECONDS);
    }

    public static void lock(String key) {
        RLock lock = getLock(key);
        lock.lock();
    }

    public static void lock(String key, long leaseTime, TimeUnit unit) {
        RLock lock = getLock(key);
        lock.lock(leaseTime, unit);
    }

    public static void unlock(String key) {
        RLock lock = getLock(key);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    public static <T> T executeWithLock(String key, long waitTime, long leaseTime, TimeUnit unit, Supplier<T> supplier) {
        boolean locked = tryLock(key, waitTime, leaseTime, unit);
        if (!locked) {
            throw new RuntimeException("获取锁失败: " + key);
        }
        try {
            return supplier.get();
        } finally {
            unlock(key);
        }
    }

    public static <T> T executeWithLock(String key, Supplier<T> supplier) {
        return executeWithLock(key, 5, 30, TimeUnit.SECONDS, supplier);
    }

    public static void executeWithLock(String key, long waitTime, long leaseTime, TimeUnit unit, Runnable runnable) {
        boolean locked = tryLock(key, waitTime, leaseTime, unit);
        if (!locked) {
            throw new RuntimeException("获取锁失败: " + key);
        }
        try {
            runnable.run();
        } finally {
            unlock(key);
        }
    }

    public static void executeWithLock(String key, Runnable runnable) {
        executeWithLock(key, 5, 30, TimeUnit.SECONDS, runnable);
    }

    public static boolean isLocked(String key) {
        RLock lock = getLock(key);
        return lock.isLocked();
    }

    public static boolean isHeldByCurrentThread(String key) {
        RLock lock = getLock(key);
        return lock.isHeldByCurrentThread();
    }
}
