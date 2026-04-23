package com.sloth.boot.starter.idempotent.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 幂等配置属性。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "sloth.idempotent")
public class IdempotentProperties {

    /**
     * 是否启用。
     */
    private boolean enabled = true;

    /**
     * 超时时间，单位秒。
     */
    private int timeout = 10;

    /**
     * Key 前缀。
     */
    private String keyPrefix = "idempotent:";
}
