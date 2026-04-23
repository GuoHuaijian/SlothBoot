package com.sloth.boot.starter.redis.monitor;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.util.Properties;

/**
 * Redis 健康检查器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class RedisHealthIndicator extends AbstractHealthIndicator {

    private final RedisConnectionFactory redisConnectionFactory;

    /**
     * 构造函数。
     *
     * @param redisConnectionFactory Redis 连接工厂
     */
    public RedisHealthIndicator(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    /**
     * 执行健康检查。
     *
     * @param builder Health 构建器
     * @throws Exception 检查异常
     */
    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            String pong = connection.ping();
            Properties memoryInfo = connection.serverCommands().info("memory");
            Properties clientsInfo = connection.serverCommands().info("clients");
            builder.up()
                    .withDetail("ping", pong)
                    .withDetail("usedMemory", memoryInfo == null ? null : memoryInfo.getProperty("used_memory_human"))
                    .withDetail("usedMemoryPeak", memoryInfo == null ? null : memoryInfo.getProperty("used_memory_peak_human"))
                    .withDetail("connectedClients", clientsInfo == null ? null : clientsInfo.getProperty("connected_clients"));
        }
    }
}
