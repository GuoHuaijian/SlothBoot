package com.sloth.boot.starter.oss.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
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
    private OssTypeEnum type = OssTypeEnum.MINIO;

    /**
     * 服务端点。
     */
    private String endpoint;

    /**
     * AccessKey。
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ToString.Exclude
    private String accessKey;

    /**
     * SecretKey。
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ToString.Exclude
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

    /**
     * 预签名 URL 过期时间（分钟）。
     */
    private int presignedUrlExpiry = 60;
}
