package com.sloth.boot.starter.feign.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Feign 配置属性。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "sloth.feign")
public class FeignProperties {

    /**
     * 是否启用 Feign Starter。
     */
    private boolean enabled = true;

    /**
     * 连接超时时间（秒）。
     */
    private long connectTimeout = 5;

    /**
     * 读取超时时间（秒）。
     */
    private long readTimeout = 10;

    /**
     * 写入超时时间（秒）。
     */
    private long writeTimeout = 10;

    /**
     * 连接池最大空闲连接数。
     */
    private int maxIdleConnections = 200;

    /**
     * 连接池空闲连接存活时间（分钟）。
     */
    private long keepAliveMinutes = 5;

    /**
     * 是否启用 Sentinel 集成。
     */
    private boolean sentinelEnabled = false;
}
