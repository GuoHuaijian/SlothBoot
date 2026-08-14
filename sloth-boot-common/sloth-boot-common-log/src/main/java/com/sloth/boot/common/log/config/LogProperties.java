package com.sloth.boot.common.log.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.HashSet;
import java.util.Set;

/**
 * 日志配置属性
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Validated
@ConfigurationProperties(prefix = "sloth.log")
public class LogProperties {

    /**
     * 是否启用
     */
    private boolean enabled = true;

    /**
     * 是否打印 HTTP 访问日志（请求/响应）
     */
    private boolean printAccessLog = true;

    /**
     * 是否打印审计日志（@OperateLog 操作记录）
     */
    private boolean printOperateLog = true;

    /**
     * 是否打印响应日志
     */
    private boolean printResponseLog = false;

    /**
     * 排除的 URL
     */
    private Set<String> excludeUrls = new HashSet<>();

    /**
     * 请求/响应体最大打印长度
     */
    private int maxBodyLength = 2048;
}
