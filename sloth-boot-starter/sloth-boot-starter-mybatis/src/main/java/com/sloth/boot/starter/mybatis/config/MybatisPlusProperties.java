package com.sloth.boot.starter.mybatis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * MyBatis Plus 配置属性。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "sloth.mybatis")
public class MybatisPlusProperties {

    /**
     * 是否启用租户插件。
     */
    private boolean tenantEnabled = false;

    /**
     * 租户字段名。
     */
    private String tenantColumn = "tenant_id";

    /**
     * 忽略租户过滤的表。
     */
    private Set<String> tenantIgnoreTables = new LinkedHashSet<>();

    /**
     * 慢 SQL 阈值，单位毫秒。
     */
    private long slowSqlThreshold = 1000L;
}
