package com.sloth.boot.common.security.xss;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

/**
 * XSS 配置属性
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "sloth.xss")
public class XssProperties {

    /**
     * 是否启用 XSS 过滤
     */
    private boolean enabled = true;

    /**
     * 排除的 URL
     */
    private Set<String> excludeUrls = new HashSet<>();

    /**
     * 是否清理 HTML 标签
     */
    private boolean cleanHtml = true;

    /**
     * 是否清理 JavaScript
     */
    private boolean cleanJavaScript = true;

    /**
     * 是否清理 CSS
     */
    private boolean cleanCss = true;

    /**
     * 是否清理事件属性
     */
    private boolean cleanEventAttributes = true;
}
