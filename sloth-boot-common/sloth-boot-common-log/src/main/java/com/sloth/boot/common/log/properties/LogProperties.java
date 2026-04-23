package com.sloth.boot.common.log.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

/**
 * 日志配置属性
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "sloth.log")
public class LogProperties {

    /**
     * 是否启用
     */
    private boolean enabled = true;

    /**
     * 是否打印请求日志
     */
    private boolean printRequestLog = true;

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
