package com.sloth.boot.starter.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Gateway 配置属性。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "sloth.gateway")
public class GatewayProperties {

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
}
