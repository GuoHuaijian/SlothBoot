package com.sloth.boot.starter.redis.lock;

import com.sloth.boot.common.exception.BizException;
import com.sloth.boot.common.exception.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 基于 Redisson 的分布式锁实现。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class RedissonDistributedLock implements DistributedLock {

    private final RedissonClient redissonClient;

    /**
     * 尝试获取分布式锁。
     *
     * @param key       锁键
     * @param waitTime  等待时间
     * @param leaseTime 锁持有时间
     * @param unit      时间单位
     * @return 是否获取成功
     */
    @Override
    public boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit) {
        try {
            return redissonClient.getLock(key).tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.error("获取分布式锁被中断, key={}", key, ex);
            throw BizException.of(GlobalErrorCode.INTERNAL_ERROR, "获取分布式锁失败");
        }
    }

    /**
     * 释放分布式锁。
     *
     * @param key 锁键
     */
    @Override
    public void unlock(String key) {
        RLock lock = redissonClient.getLock(key);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    /**
     * 在锁保护下执行任务。
     *
     * @param key       锁键
     * @param waitTime  等待时间
     * @param leaseTime 锁持有时间
     * @param supplier  业务逻辑
     * @param <T>       返回值类型
     * @return 执行结果
     */
    @Override
    public <T> T executeWithLock(String key, long waitTime, long leaseTime, Supplier<T> supplier) {
        boolean locked = tryLock(key, waitTime, leaseTime, TimeUnit.SECONDS);
        if (!locked) {
            throw BizException.of(GlobalErrorCode.REPEATED_REQUEST, "获取分布式锁失败");
        }
        try {
            return supplier.get();
        } finally {
            unlock(key);
        }
    }
}
