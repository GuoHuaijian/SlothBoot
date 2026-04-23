package com.sloth.boot.starter.thread.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 线程池配置属性。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "sloth.thread-pool")
public class ThreadPoolProperties {

    /**
     * 是否启用线程池 starter。
     */
    private boolean enabled = true;

    /**
     * 是否启用动态配置。
     */
    private boolean dynamic = true;

    /**
     * 多线程池配置。
     */
    private Map<String, PoolConfig> pools = buildDefaultPools();

    private Map<String, PoolConfig> buildDefaultPools() {
        Map<String, PoolConfig> poolConfigs = new LinkedHashMap<>();
        PoolConfig defaultPool = new PoolConfig();
        defaultPool.setCoreSize(8);
        defaultPool.setMaxSize(32);
        defaultPool.setQueueCapacity(1024);
        defaultPool.setKeepAliveTime(60);
        defaultPool.setThreadNamePrefix("sloth-async-");
        defaultPool.setRejectedPolicy("CALLER_RUNS");
        poolConfigs.put("default", defaultPool);

        PoolConfig scheduledPool = new PoolConfig();
        scheduledPool.setCoreSize(4);
        scheduledPool.setMaxSize(4);
        scheduledPool.setQueueCapacity(0);
        scheduledPool.setKeepAliveTime(60);
        scheduledPool.setThreadNamePrefix("sloth-scheduled-");
        scheduledPool.setRejectedPolicy("CALLER_RUNS");
        poolConfigs.put("scheduled", scheduledPool);
        return poolConfigs;
    }

    /**
     * 单个线程池配置。
     *
     * @author sloth-boot
     * @since 1.0.0
     */
    @Data
    public static class PoolConfig {

        /**
         * 核心线程数。
         */
        private int coreSize = 8;

        /**
         * 最大线程数。
         */
        private int maxSize = 32;

        /**
         * 队列容量。
         */
        private int queueCapacity = 1024;

        /**
         * 空闲线程存活时间，单位秒。
         */
        private int keepAliveTime = 60;

        /**
         * 线程名前缀。
         */
        private String threadNamePrefix = "sloth-async-";

        /**
         * 拒绝策略。
         */
        private String rejectedPolicy = "CALLER_RUNS";
    }
}
