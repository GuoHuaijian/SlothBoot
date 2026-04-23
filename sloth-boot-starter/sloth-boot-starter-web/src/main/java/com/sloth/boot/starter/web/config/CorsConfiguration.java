package com.sloth.boot.starter.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 跨域配置。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "sloth.web.cors")
public class CorsConfiguration {

    /**
     * 允许的来源。
     */
    private Set<String> allowedOrigins = new LinkedHashSet<>();

    /**
     * 允许的方法。
     */
    private List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS");

    /**
     * 允许的头。
     */
    private List<String> allowedHeaders = List.of("*");

    /**
     * 是否允许携带 Cookie。
     */
    private boolean allowCredentials = true;

    /**
     * 最大年龄。
     */
    private long maxAge = 3600;
}
