package com.sloth.boot.starter.feign.config;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * OkHttp 配置。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(OkHttpClient.class)
@ConditionalOnProperty(prefix = "sloth.feign", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(FeignProperties.class)
public class OkHttpConfig {

    /**
     * 注册连接池。
     *
     * @param feignProperties Feign 配置
     * @return 连接池
     */
    @Bean
    @ConditionalOnMissingBean
    public ConnectionPool okHttpConnectionPool(FeignProperties feignProperties) {
        return new ConnectionPool(
            feignProperties.getMaxIdleConnections(),
            feignProperties.getKeepAliveMinutes(),
            TimeUnit.MINUTES);
    }

    /**
     * 注册 OkHttpClient。
     *
     * @param connectionPool  连接池
     * @param feignProperties Feign 配置
     * @return OkHttpClient
     */
    @Bean
    @ConditionalOnMissingBean
    public OkHttpClient okHttpClient(ConnectionPool connectionPool, FeignProperties feignProperties) {
        return new OkHttpClient.Builder()
            .connectionPool(connectionPool)
            .connectTimeout(feignProperties.getConnectTimeout(), TimeUnit.SECONDS)
            .readTimeout(feignProperties.getReadTimeout(), TimeUnit.SECONDS)
            .writeTimeout(feignProperties.getWriteTimeout(), TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build();
    }
}
