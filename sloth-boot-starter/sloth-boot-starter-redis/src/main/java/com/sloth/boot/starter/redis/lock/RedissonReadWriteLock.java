package com.sloth.boot.starter.redis.lock;

import com.sloth.boot.common.exception.BizException;
import com.sloth.boot.common.exception.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 基于 Redisson 的分布式读写锁实现。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class RedissonReadWriteLock implements DistributedReadWriteLock {

    private final RedissonClient redissonClient;

    /**
     * 尝试获取读锁。
     *
     * @param key       锁的 key
     * @param waitTime  最大等待时间
     * @param leaseTime 锁的持有时间
     * @param unit      时间单位
     * @return 是否获取成功
     */
    @Override
    public boolean tryReadLock(String key, long waitTime, long leaseTime, TimeUnit unit) {
        try {
            RLock readLock = getReadWriteLock(key).readLock();
            return readLock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw BizException.of(GlobalErrorCode.INTERNAL_ERROR);
        }
    }

    /**
     * 尝试获取写锁。
     *
     * @param key       锁的 key
     * @param waitTime  最大等待时间
     * @param leaseTime 锁的持有时间
     * @param unit      时间单位
     * @return 是否获取成功
     */
    @Override
    public boolean tryWriteLock(String key, long waitTime, long leaseTime, TimeUnit unit) {
        try {
            RLock writeLock = getReadWriteLock(key).writeLock();
            return writeLock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw BizException.of(GlobalErrorCode.INTERNAL_ERROR);
        }
    }

    /**
     * 释放读锁。
     *
     * @param key 锁的 key
     */
    @Override
    public void unlockRead(String key) {
        RLock readLock = getReadWriteLock(key).readLock();
        if (readLock.isHeldByCurrentThread()) {
            readLock.unlock();
        }
    }

    /**
     * 释放写锁。
     *
     * @param key 锁的 key
     */
    @Override
    public void unlockWrite(String key) {
        RLock writeLock = getReadWriteLock(key).writeLock();
        if (writeLock.isHeldByCurrentThread()) {
            writeLock.unlock();
        }
    }

    /**
     * 在读锁保护下执行操作。
     *
     * @param key       锁的 key
     * @param waitTime  最大等待时间
     * @param leaseTime 锁的持有时间
     * @param unit      时间单位
     * @param supplier  要执行的操作
     * @param <T>       返回值类型
     * @return 操作结果
     */
    @Override
    public <T> T executeWithReadLock(String key, long waitTime, long leaseTime, TimeUnit unit, Supplier<T> supplier) {
        boolean locked = tryReadLock(key, waitTime, leaseTime, unit);
        if (!locked) {
            throw BizException.of(GlobalErrorCode.REPEATED_REQUEST);
        }
        try {
            return supplier.get();
        } finally {
            unlockRead(key);
        }
    }

    /**
     * 在写锁保护下执行操作。
     *
     * @param key       锁的 key
     * @param waitTime  最大等待时间
     * @param leaseTime 锁的持有时间
     * @param unit      时间单位
     * @param supplier  要执行的操作
     * @param <T>       返回值类型
     * @return 操作结果
     */
    @Override
    public <T> T executeWithWriteLock(String key, long waitTime, long leaseTime, TimeUnit unit, Supplier<T> supplier) {
        boolean locked = tryWriteLock(key, waitTime, leaseTime, unit);
        if (!locked) {
            throw BizException.of(GlobalErrorCode.REPEATED_REQUEST);
        }
        try {
            return supplier.get();
        } finally {
            unlockWrite(key);
        }
    }

    /**
     * 获取 Redisson 读写锁实例。
     *
     * @param key 锁的 key
     * @return 读写锁
     */
    private RReadWriteLock getReadWriteLock(String key) {
        return redissonClient.getReadWriteLock(key);
    }
}
