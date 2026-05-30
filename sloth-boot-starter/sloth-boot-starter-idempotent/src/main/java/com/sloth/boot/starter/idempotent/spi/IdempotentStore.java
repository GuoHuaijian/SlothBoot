package com.sloth.boot.starter.idempotent.spi;

import java.time.Duration;

/**
 * 幂等存储 SPI。
 * <p>
 * 定义幂等操作的存储抽象，默认实现基于 Redis。
 * 业务方可提供自定义实现（如数据库、内存）覆盖默认 Bean。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public interface IdempotentStore {

    /**
     * 尝试获取幂等锁。
     *
     * @param key       幂等键
     * @param requestId 请求唯一标识
     * @param timeout   锁过期时间
     * @return true 获取成功（首次请求），false 获取失败（重复请求）
     */
    boolean tryAcquire(String key, String requestId, Duration timeout);

    /**
     * 释放幂等锁（仅当锁仍为当前 requestId 持有时才释放）。
     *
     * @param key       幂等键
     * @param requestId 请求唯一标识
     * @return true 释放成功
     */
    boolean release(String key, String requestId);

    /**
     * 消费幂等 Token（Token 模式，一次性使用）。
     *
     * @param key Token 键
     * @return true 消费成功（首次），false Token 无效或已消费
     */
    boolean consumeToken(String key);
}
