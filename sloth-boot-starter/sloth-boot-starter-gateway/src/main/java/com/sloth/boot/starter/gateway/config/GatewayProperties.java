package com.sloth.boot.starter.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Gateway 配置属性。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Validated
@ConfigurationProperties(prefix = "sloth.gateway")
public class GatewayProperties {

    /**
     * 是否启用 Gateway Starter。
     */
    private boolean enabled = true;

    /**
     * 白名单路径。
     */
    private Set<String> whiteList = new LinkedHashSet<>();

    /**
     * 是否启用动态路由。
     */
    private boolean dynamicRouteEnabled = true;

    /**
     * IP 黑名单。
     */
    private Set<String> blackList = new LinkedHashSet<>();

    /**
     * 重试配置。
     */
    private RetryConfig retry = new RetryConfig();

    /**
     * 重试配置对象。
     *
     * @author sloth-boot
     * @since 1.0.0
     */
    @Data
    public static class RetryConfig {

        /**
         * 是否启用重试。
         */
        private boolean enabled = false;

        /**
         * 最大重试次数。
         */
        private int maxAttempts = 3;

        /**
         * 退避间隔，单位毫秒。
         */
        private long backoffMs = 100;
    }
}
