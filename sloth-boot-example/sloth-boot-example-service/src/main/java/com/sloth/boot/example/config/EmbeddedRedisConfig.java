package com.sloth.boot.example.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import redis.embedded.RedisServer;

/**
 * 内嵌 Redis 配置。
 * <p>
 * 示例模块启动时自动启动内嵌 Redis 服务器，无需外部 Redis 依赖。
 * 使用独立端口 {@code 16379} 避免与本地 Redis 冲突。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class EmbeddedRedisConfig {

    private static final int EMBEDDED_REDIS_PORT = 16379;

    @Value("${spring.data.redis.port:" + EMBEDDED_REDIS_PORT + "}")
    private int configuredPort;

    private RedisServer redisServer;

    /**
     * 启动内嵌 Redis 服务器。
     */
    @PostConstruct
    public void startRedis() {
        if (configuredPort == EMBEDDED_REDIS_PORT) {
            try {
                redisServer = RedisServer.builder()
                        .port(EMBEDDED_REDIS_PORT)
                        .setting("maxmemory 16mb")
                        .build();
                redisServer.start();
                log.info("[Example] 内嵌 Redis 已启动, 端口: {}", EMBEDDED_REDIS_PORT);
            } catch (Exception e) {
                log.warn("[Example] 内嵌 Redis 启动失败（可能端口已被占用）: {}", e.getMessage());
            }
        }
    }

    /**
     * 注册 Redis 连接工厂，连接到内嵌 Redis。
     *
     * @return Redis 连接工厂
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory("127.0.0.1", EMBEDDED_REDIS_PORT);
    }

    /**
     * 停止内嵌 Redis 服务器。
     */
    @PreDestroy
    public void stopRedis() {
        if (redisServer != null && redisServer.isActive()) {
            redisServer.stop();
            log.info("[Example] 内嵌 Redis 已停止");
        }
    }
}
