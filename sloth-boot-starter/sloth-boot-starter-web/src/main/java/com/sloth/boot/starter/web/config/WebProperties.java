package com.sloth.boot.starter.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Web 模块配置属性。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "sloth.web")
public class WebProperties {

    /**
     * 是否启用统一响应包装。
     */
    private boolean responseWrapper = true;

    /**
     * 是否启用请求体缓存（支持多次读取 @RequestBody）。
     */
    private boolean bodyCacheEnabled = false;

    /**
     * 是否启用 API 访问日志事件发布。
     */
    private boolean accessLogEnabled = true;
}
