package com.sloth.boot.starter.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Gateway CORS 跨域配置属性。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Validated
@ConfigurationProperties(prefix = "sloth.gateway.cors")
public class GatewayCorsProperties {

    /**
     * 是否启用跨域。
     */
    private boolean enabled = true;

    /**
     * 允许的来源（支持通配符）。
     */
    private List<String> allowedOrigins = List.of("http://localhost:*");

    /**
     * 允许的请求头。
     */
    private List<String> allowedHeaders = List.of("*");

    /**
     * 允许的 HTTP 方法。
     */
    private List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS");

    /**
     * 是否允许携带凭证。
     */
    private boolean allowCredentials = true;

    /**
     * 预检请求缓存时间（秒）。
     */
    private long maxAge = 3600L;
}
