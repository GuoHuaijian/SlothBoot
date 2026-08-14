package com.sloth.boot.starter.es.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Elasticsearch 配置属性。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Validated
@ConfigurationProperties(prefix = "sloth.es")
public class EsProperties {

    /**
     * 是否启用。
     */
    private boolean enabled = true;

    /**
     * 默认索引名。
     */
    private String defaultIndex;

    /**
     * 查询超时时间，单位秒。
     */
    private long timeout = 5L;

    /**
     * 慢查询阈值，单位秒。超过此值记录 WARN 日志。
     */
    private long slowQueryThreshold = 3L;

    /**
     * 批量操作每批数量。
     */
    private int bulkSize = 1000;

    /**
     * 滚动查询每批大小。
     */
    private int scrollSize = 500;

    /**
     * 滚动上下文保留时间。
     */
    private String scrollKeepAlive = "1m";

    /**
     * 索引前缀，所有自动解析的索引名会添加此前缀。
     */
    private String indexPrefix;

    /**
     * 时间索引日期格式，默认按天。
     */
    private String indexDateFormat = "yyyy.MM.dd";

    /**
     * 是否启用操作审计日志。
     */
    private boolean auditLogEnabled = false;

    /**
     * 更新冲突重试次数。
     */
    private int retryOnConflict = 3;
}
