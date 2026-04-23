package com.sloth.boot.starter.oss.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OSS 配置属性。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "sloth.oss")
public class OssProperties {

    /**
     * OSS 类型。
     */
    private String type = "minio";

    /**
     * 服务端点。
     */
    private String endpoint;

    /**
     * AccessKey。
     */
    private String accessKey;

    /**
     * SecretKey。
     */
    private String secretKey;

    /**
     * Bucket 名称。
     */
    private String bucketName;

    /**
     * 区域。
     */
    private String region;

    /**
     * 访问域名。
     */
    private String domain;
}
