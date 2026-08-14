package com.sloth.boot.starter.seata.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Seata 配置属性。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Validated
@ConfigurationProperties(prefix = "sloth.seata")
public class SeataProperties {

    /**
     * 是否启用。
     */
    private boolean enabled = false;

    /**
     * 事务分组。
     */
    private String txServiceGroup = "${spring.application.name}-tx-group";

    /**
     * 事务模式。
     */
    private String mode = "AT";
}
