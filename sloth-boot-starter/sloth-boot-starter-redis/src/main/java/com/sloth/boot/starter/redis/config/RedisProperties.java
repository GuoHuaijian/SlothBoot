package com.sloth.boot.starter.redis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Redis 配置属性。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "sloth.redis")
public class RedisProperties {

    /**
     * 是否启用 Redis Starter。
     */
    private boolean enabled = true;

    /**
     * 统一业务 key 前缀。
     */
    private String keyPrefix = "sloth:";

    /**
     * 分布式锁默认等待时间，单位秒。
     */
    private long lockWaitTime = 3L;

    /**
     * 分布式锁默认租约时间，单位秒。
     */
    private long lockLeaseTime = 30L;

    /**
     * 是否携带类型信息进行 JSON 序列化。
     */
    private boolean enableTypeInfo = true;

    /**
     * 空值缓存时间，单位秒。
     */
    private long nullValueExpireSeconds = 60L;

    /**
     * 多级缓存配置。
     */
    private MultiCache multiCache = new MultiCache();

    /**
     * 分布式 ID 生成器配置。
     */
    private IdGenerator idGenerator = new IdGenerator();

    /**
     * 多级缓存配置。
     */
    @Data
    public static class MultiCache {

        /**
         * 是否启用多级缓存（Caffeine + Redis）。
         */
        private boolean enabled = false;

        /**
         * L1 Caffeine 缓存最大条目数。
         */
        private int l1MaxSize = 1000;

        /**
         * L1 Caffeine 缓存过期时间（秒）。
         */
        private long l1TtlSeconds = 300;
    }

    /**
     * 分布式 ID 生成器配置。
     */
    @Data
    public static class IdGenerator {

        /**
         * 是否启用分布式 ID 生成器。
         */
        private boolean enabled = true;

        /**
         * 机器号（0-1023）。
         */
        private int workerId = 0;

        /**
         * ID 前缀。
         */
        private String prefix = "sloth";
    }
}
