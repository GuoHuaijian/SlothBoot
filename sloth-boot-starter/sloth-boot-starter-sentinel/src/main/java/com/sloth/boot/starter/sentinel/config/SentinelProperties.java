package com.sloth.boot.starter.sentinel.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Sentinel 配置属性。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "sloth.sentinel")
public class SentinelProperties {

    /**
     * 是否启用 Sentinel Starter。
     */
    private boolean enabled = true;

    /**
     * 数据源类型。
     */
    private String datasource = "nacos";

    /**
     * Nacos Group ID。
     */
    private String nacosGroupId = "SENTINEL_GROUP";
}
