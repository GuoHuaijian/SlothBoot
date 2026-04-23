package com.sloth.boot.starter.feign.config;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
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
public class OkHttpConfig {

    /**
     * 注册连接池。
     *
     * @return 连接池
     */
    @Bean
    public ConnectionPool okHttpConnectionPool() {
        return new ConnectionPool(200, 5, TimeUnit.MINUTES);
    }

    /**
     * 注册 OkHttpClient。
     *
     * @param connectionPool 连接池
     * @return OkHttpClient
     */
    @Bean
    public OkHttpClient okHttpClient(ConnectionPool connectionPool) {
        return new OkHttpClient.Builder()
                .connectionPool(connectionPool)
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }
}
