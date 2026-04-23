package com.sloth.boot.starter.web.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Web 模块配置属性。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "sloth.web")
public class SlothWebProperties {

    /**
     * 是否启用统一响应包装。
     */
    private boolean responseWrapper = true;

    /**
     * 是否启用 XSS 防护。
     */
    private boolean xssEnabled = true;

    /**
     * XSS 排除 URL。
     */
    private Set<String> xssExcludeUrls = new LinkedHashSet<>();
}
