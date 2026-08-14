package com.sloth.boot.starter.oss.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.sloth.boot.starter.oss.core.AliyunOssClient;
import com.sloth.boot.starter.oss.core.LocalOssClient;
import com.sloth.boot.starter.oss.core.MinioOssClient;
import com.sloth.boot.starter.oss.core.OssClient;
import com.sloth.boot.starter.oss.core.OssTemplate;
import io.minio.MinioClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * OSS 自动配置。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "sloth.oss", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(OssProperties.class)
public class OssAutoConfiguration {

    /**
     * 注册 OSS 客户端。
     *
     * @param properties 配置
     * @return OSS 客户端
     */
    @Bean
    @ConditionalOnMissingBean
    public OssClient ossClient(OssProperties properties) {
        return switch (properties.getType()) {
            case ALIYUN -> {
                Assert.hasText(properties.getEndpoint(), "sloth.oss.endpoint must not be blank for aliyun type");
                OSS oss = new OSSClientBuilder().build(
                    properties.getEndpoint(),
                    properties.getAccessKey(),
                    properties.getSecretKey()
                );
                yield new AliyunOssClient(oss, properties);
            }
            case MINIO -> {
                Assert.hasText(properties.getEndpoint(), "sloth.oss.endpoint must not be blank for minio type");
                MinioClient minioClient = MinioClient.builder()
                    .endpoint(properties.getEndpoint())
                    .credentials(properties.getAccessKey(), properties.getSecretKey())
                    .build();
                yield new MinioOssClient(minioClient, properties);
            }
            case LOCAL -> new LocalOssClient(properties);
        };
    }

    /**
     * 注册 OSS 模板。
     *
     * @param ossClient OSS 客户端
     * @return OSS 模板
     */
    @Bean
    @ConditionalOnMissingBean
    public OssTemplate ossTemplate(OssClient ossClient) {
        return new OssTemplate(ossClient);
    }
}
