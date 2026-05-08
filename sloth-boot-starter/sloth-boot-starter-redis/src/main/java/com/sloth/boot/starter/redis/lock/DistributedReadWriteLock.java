package com.sloth.boot.starter.redis.lock;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 分布式读写锁接口。
 * <p>
 * 读锁：共享锁，多个读操作可并发执行。
 * 写锁：排他锁，与任何其他锁互斥。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public interface DistributedReadWriteLock {

    /**
     * 尝试获取读锁。
     *
     * @param key       锁的 key
     * @param waitTime  最大等待时间
     * @param leaseTime 锁的持有时间
     * @param unit      时间单位
     * @return 是否获取成功
     */
    boolean tryReadLock(String key, long waitTime, long leaseTime, TimeUnit unit);

    /**
     * 尝试获取写锁。
     *
     * @param key       锁的 key
     * @param waitTime  最大等待时间
     * @param leaseTime 锁的持有时间
     * @param unit      时间单位
     * @return 是否获取成功
     */
    boolean tryWriteLock(String key, long waitTime, long leaseTime, TimeUnit unit);

    /**
     * 释放读锁。
     *
     * @param key 锁的 key
     */
    void unlockRead(String key);

    /**
     * 释放写锁。
     *
     * @param key 锁的 key
     */
    void unlockWrite(String key);

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
    <T> T executeWithReadLock(String key, long waitTime, long leaseTime, TimeUnit unit, Supplier<T> supplier);

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
    <T> T executeWithWriteLock(String key, long waitTime, long leaseTime, TimeUnit unit, Supplier<T> supplier);
}
