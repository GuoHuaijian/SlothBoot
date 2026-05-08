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

    @Override
    public void unlockRead(String key) {
        RLock readLock = getReadWriteLock(key).readLock();
        if (readLock.isHeldByCurrentThread()) {
            readLock.unlock();
        }
    }

    @Override
    public void unlockWrite(String key) {
        RLock writeLock = getReadWriteLock(key).writeLock();
        if (writeLock.isHeldByCurrentThread()) {
            writeLock.unlock();
        }
    }

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

    private RReadWriteLock getReadWriteLock(String key) {
        return redissonClient.getReadWriteLock(key);
    }
}
