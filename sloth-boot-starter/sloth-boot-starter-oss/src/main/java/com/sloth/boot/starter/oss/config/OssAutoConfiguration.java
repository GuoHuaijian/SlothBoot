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
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * OSS 自动配置。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfiguration
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
        String type = properties.getType();
        if ("aliyun".equalsIgnoreCase(type)) {
            OSS oss = new OSSClientBuilder().build(
                    properties.getEndpoint(),
                    properties.getAccessKey(),
                    properties.getSecretKey()
            );
            return new AliyunOssClient(oss, properties);
        }
        if ("minio".equalsIgnoreCase(type)) {
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(properties.getEndpoint())
                    .credentials(properties.getAccessKey(), properties.getSecretKey())
                    .build();
            return new MinioOssClient(minioClient, properties);
        }
        return new LocalOssClient(properties);
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
