package com.sloth.boot.starter.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;

/**
 * Gzip 压缩配置。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Validated
@ConfigurationProperties(prefix = "sloth.web.gzip")
public class GzipProperties {

    /**
     * 是否启用 Gzip 压缩。
     */
    private boolean enabled = false;

    /**
     * 启用压缩的最小响应体大小（字节）。
     */
    @Min(1)
    private int minSize = 1024;

    /**
     * 启用压缩的 MIME 类型。
     */
    private String[] mimeTypes =
        {"text/html", "text/xml", "text/plain", "text/css", "application/json", "application/javascript"};
}
