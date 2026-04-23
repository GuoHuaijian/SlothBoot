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
}
