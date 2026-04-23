package com.sloth.boot.starter.es.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Elasticsearch 配置属性。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "sloth.es")
public class EsProperties {

    /**
     * 是否启用。
     */
    private boolean enabled = true;

    /**
     * 默认索引。
     */
    private String defaultIndex;

    /**
     * 查询超时时间，单位秒。
     */
    private long timeout = 5L;
}
