package com.sloth.boot.starter.redis.lock;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 分布式锁接口。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public interface DistributedLock {

    /**
     * 尝试获取分布式锁。
     *
     * @param key       锁键
     * @param waitTime  等待时间
     * @param leaseTime 锁持有时间
     * @param unit      时间单位
     * @return 是否获取成功
     */
    boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit);

    /**
     * 释放分布式锁。
     *
     * @param key 锁键
     */
    void unlock(String key);

    /**
     * 在锁保护下执行任务。
     *
     * @param key       锁键
     * @param waitTime  等待时间
     * @param leaseTime 锁持有时间
     * @param supplier  业务逻辑
     * @param <T>       返回值类型
     * @return 业务执行结果
     */
    <T> T executeWithLock(String key, long waitTime, long leaseTime, Supplier<T> supplier);
}
