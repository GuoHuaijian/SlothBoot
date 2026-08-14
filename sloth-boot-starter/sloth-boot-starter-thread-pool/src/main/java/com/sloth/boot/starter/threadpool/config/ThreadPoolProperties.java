package com.sloth.boot.starter.threadpool.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 线程池配置属性。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Validated
@ConfigurationProperties(prefix = "sloth.thread-pool")
public class ThreadPoolProperties {

    /**
     * 是否启用线程池 starter。
     */
    private boolean enabled = true;

    /**
     * 是否启用 Java 21 虚拟线程。
     * <p>
     * 启用后将注册基于虚拟线程的 Executor，适用于 I/O 密集型任务。
     * 需要 JDK 21+ 且 JVM 参数 {@code --enable-preview}（JDK 21 正式版无需）。
     */
    private boolean virtualEnabled = false;

    /**
     * 告警配置。
     */
    private AlarmConfig alarm = new AlarmConfig();

    /**
     * 多线程池配置。
     */
    private Map<String, PoolConfig> pools = buildDefaultPools();

    private Map<String, PoolConfig> buildDefaultPools() {
        Map<String, PoolConfig> poolConfigs = new LinkedHashMap<>();

        // 默认异步线程池
        PoolConfig defaultPool = new PoolConfig();
        defaultPool.setCoreSize(8);
        defaultPool.setMaxSize(32);
        defaultPool.setQueueCapacity(1024);
        defaultPool.setKeepAliveTime(60);
        defaultPool.setThreadNamePrefix("sloth-async-");
        defaultPool.setRejectedPolicy("CALLER_RUNS");
        poolConfigs.put("default", defaultPool);

        // 定时任务线程池
        PoolConfig scheduledPool = new PoolConfig();
        scheduledPool.setCoreSize(4);
        scheduledPool.setMaxSize(4);
        scheduledPool.setQueueCapacity(0);
        scheduledPool.setKeepAliveTime(60);
        scheduledPool.setThreadNamePrefix("sloth-scheduled-");
        scheduledPool.setRejectedPolicy("CALLER_RUNS");
        poolConfigs.put("scheduled", scheduledPool);

        // HTTP 客户端线程池（适用于 Feign/RestTemplate 异步调用）
        PoolConfig httpClientPool = new PoolConfig();
        httpClientPool.setCoreSize(4);
        httpClientPool.setMaxSize(16);
        httpClientPool.setQueueCapacity(256);
        httpClientPool.setKeepAliveTime(60);
        httpClientPool.setThreadNamePrefix("sloth-http-");
        httpClientPool.setRejectedPolicy("CALLER_RUNS");
        poolConfigs.put("http-client", httpClientPool);

        // 消息队列消费线程池
        PoolConfig mqConsumerPool = new PoolConfig();
        mqConsumerPool.setCoreSize(4);
        mqConsumerPool.setMaxSize(16);
        mqConsumerPool.setQueueCapacity(512);
        mqConsumerPool.setKeepAliveTime(60);
        mqConsumerPool.setThreadNamePrefix("sloth-mq-");
        mqConsumerPool.setRejectedPolicy("CALLER_RUNS");
        poolConfigs.put("mq-consumer", mqConsumerPool);

        // 数据同步线程池（适用于批量导入、ES 同步等）
        PoolConfig dataSyncPool = new PoolConfig();
        dataSyncPool.setCoreSize(2);
        dataSyncPool.setMaxSize(8);
        dataSyncPool.setQueueCapacity(1024);
        dataSyncPool.setKeepAliveTime(120);
        dataSyncPool.setThreadNamePrefix("sloth-sync-");
        dataSyncPool.setRejectedPolicy("CALLER_RUNS");
        poolConfigs.put("data-sync", dataSyncPool);

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

    /**
     * 告警配置。
     *
     * @author sloth-boot
     * @since 1.0.0
     */
    @Data
    public static class AlarmConfig {

        /**
         * 队列使用率告警阈值（百分比）。
         */
        private double threshold = 80.0;
    }
}
