package com.sloth.boot.starter.threadpool.config;

import com.sloth.boot.starter.threadpool.async.AsyncExceptionHandler;
import com.sloth.boot.starter.threadpool.core.ThreadPoolManager;
import com.sloth.boot.starter.threadpool.core.ThreadPoolRegistry;
import com.sloth.boot.starter.threadpool.core.VisibleThreadPoolExecutor;
import com.sloth.boot.starter.threadpool.decorator.TtlTaskDecorator;
import com.sloth.boot.starter.threadpool.metrics.ThreadPoolAlarmTask;
import com.sloth.boot.starter.threadpool.metrics.ThreadPoolEndpoint;
import com.sloth.boot.starter.threadpool.metrics.ThreadPoolMetrics;
import com.sloth.boot.starter.threadpool.reject.LogRejectedExecutionHandler;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池自动配置。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfiguration
@EnableConfigurationProperties(ThreadPoolProperties.class)
@ConditionalOnProperty(prefix = "sloth.thread-pool", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ThreadPoolAutoConfiguration {

    /**
     * 注册线程池注册表。
     *
     * @return 注册表
     */
    @Bean
    @ConditionalOnMissingBean
    public ThreadPoolRegistry threadPoolRegistry() {
        return new ThreadPoolRegistry();
    }

    /**
     * 注册任务装饰器。
     *
     * @return 任务装饰器
     */
    @Bean
    @ConditionalOnMissingBean
    public TaskDecorator taskDecorator() {
        return new TtlTaskDecorator();
    }

    /**
     * 注册默认线程池执行器代理。
     *
     * @param properties         配置
     * @param threadPoolRegistry 注册表
     * @return 可观测线程池
     */
    @Bean(name = "slothTaskExecutor")
    @ConditionalOnMissingBean(name = "slothTaskExecutor")
    public VisibleThreadPoolExecutor slothTaskExecutor(ThreadPoolProperties properties,
                                                       ThreadPoolRegistry threadPoolRegistry) {
        ThreadPoolProperties.PoolConfig poolConfig = properties.getPools().getOrDefault("default", new ThreadPoolProperties.PoolConfig());
        VisibleThreadPoolExecutor executor = buildExecutor("default", poolConfig);
        threadPoolRegistry.register("default", executor);
        return executor;
    }

    /**
     * 注册虚拟线程执行器（Java 21+）。
     * <p>
     * 仅当 {@code sloth.thread-pool.virtual-enabled=true} 且 JVM 支持虚拟线程时生效。
     * 虚拟线程适用于 I/O 密集型任务，不适合 CPU 密集型任务。
     *
     * @return 虚拟线程执行器
     */
    @Bean(name = "slothVirtualThreadExecutor")
    @ConditionalOnMissingBean(name = "slothVirtualThreadExecutor")
    @ConditionalOnProperty(prefix = "sloth.thread-pool", name = "virtual-enabled", havingValue = "true")
    public ExecutorService slothVirtualThreadExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    /**
     * 注册异步配置器。
     *
     * @param executor              默认线程池
     * @param asyncExceptionHandler 异步异常处理器
     * @return 异步配置器
     */
    @Bean
    @ConditionalOnMissingBean
    public AsyncConfigurer asyncConfigurer(VisibleThreadPoolExecutor executor, AsyncExceptionHandler asyncExceptionHandler) {
        return new AsyncConfigurer() {
            @Override
            public java.util.concurrent.Executor getAsyncExecutor() {
                return executor;
            }

            @Override
            public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
                return asyncExceptionHandler;
            }
        };
    }

    /**
     * 注册异步异常处理器。
     *
     * @return 异步异常处理器
     */
    @Bean
    @ConditionalOnMissingBean
    public AsyncExceptionHandler asyncExceptionHandler() {
        return new AsyncExceptionHandler();
    }

    /**
     * 注册定时任务线程池。
     *
     * @param properties         配置
     * @param threadPoolRegistry 注册表
     * @return 定时线程池
     */
    @Bean
    @ConditionalOnMissingBean
    public ScheduledThreadPoolExecutor scheduledThreadPoolExecutor(ThreadPoolProperties properties,
                                                                   ThreadPoolRegistry threadPoolRegistry) {
        ThreadPoolProperties.PoolConfig poolConfig = properties.getPools().getOrDefault("scheduled", new ThreadPoolProperties.PoolConfig());
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(
            poolConfig.getCoreSize(),
            buildThreadFactory(poolConfig.getThreadNamePrefix()),
            new LogRejectedExecutionHandler("scheduled", true)
        );
        executor.setKeepAliveTime(poolConfig.getKeepAliveTime(), TimeUnit.SECONDS);
        executor.setRemoveOnCancelPolicy(true);
        return executor;
    }

    /**
     * 注册指标采集。
     *
     * @param meterRegistry      指标注册中心
     * @param threadPoolRegistry 线程池注册表
     * @return 指标采集器
     */
    @Bean
    @ConditionalOnClass(MeterRegistry.class)
    @ConditionalOnBean(MeterRegistry.class)
    @ConditionalOnMissingBean
    public ThreadPoolMetrics threadPoolMetrics(MeterRegistry meterRegistry, ThreadPoolRegistry threadPoolRegistry) {
        return new ThreadPoolMetrics(meterRegistry, threadPoolRegistry);
    }

    /**
     * 注册线程池端点。
     *
     * @param threadPoolRegistry 注册表
     * @return 线程池端点
     */
    @Bean
    @ConditionalOnClass(Endpoint.class)
    @ConditionalOnMissingBean
    public ThreadPoolEndpoint threadPoolEndpoint(ThreadPoolRegistry threadPoolRegistry,
                                                 ThreadPoolManager threadPoolManager) {
        return new ThreadPoolEndpoint(threadPoolRegistry, threadPoolManager);
    }

    private VisibleThreadPoolExecutor buildExecutor(String poolName, ThreadPoolProperties.PoolConfig poolConfig) {
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(poolConfig.getQueueCapacity());
        LogRejectedExecutionHandler rejectedHandler = new LogRejectedExecutionHandler(
            poolName,
            "CALLER_RUNS".equalsIgnoreCase(poolConfig.getRejectedPolicy())
        );
        return new VisibleThreadPoolExecutor(
            poolName,
            poolConfig.getCoreSize(),
            poolConfig.getMaxSize(),
            poolConfig.getKeepAliveTime(),
            TimeUnit.SECONDS,
            queue,
            buildThreadFactory(poolConfig.getThreadNamePrefix()),
            (runnable, executor) -> {
                if (executor instanceof VisibleThreadPoolExecutor visibleThreadPoolExecutor) {
                    visibleThreadPoolExecutor.incrementRejectedCount();
                }
                rejectedHandler.rejectedExecution(runnable, executor);
            }
        );
    }

    private ThreadFactory buildThreadFactory(String prefix) {
        AtomicInteger counter = new AtomicInteger(1);
        return runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName(prefix + counter.getAndIncrement());
            thread.setDaemon(true);
            return thread;
        };
    }


    /**
     * 注册线程池动态管理器。
     *
     * @param threadPoolRegistry 线程池注册表
     * @return 线程池管理器
     */
    @Bean
    @ConditionalOnMissingBean
    public ThreadPoolManager threadPoolManager(ThreadPoolRegistry threadPoolRegistry) {
        return new ThreadPoolManager(threadPoolRegistry);
    }

    /**
     * 注册线程池队列使用率告警任务。
     *
     * @param threadPoolRegistry 线程池注册表
     * @param eventPublisher     事件发布器
     * @return 告警任务
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "sloth.thread-pool", name = "alarm-enabled", havingValue = "true")
    public ThreadPoolAlarmTask threadPoolAlarmTask(ThreadPoolRegistry threadPoolRegistry,
                                                    org.springframework.context.ApplicationEventPublisher eventPublisher,
                                                    ThreadPoolProperties properties) {
        return new ThreadPoolAlarmTask(threadPoolRegistry, eventPublisher, properties.getAlarm().getThreshold());
    }

    /**
     * 异步支持配置。
     * <p>
     * 默认开启。如不需要 @Async 支持，可通过 sloth.thread-pool.async-enabled=false 关闭。
     */
    @org.springframework.context.annotation.Configuration
    @org.springframework.scheduling.annotation.EnableAsync
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(
        prefix = "sloth.thread-pool", name = "async-enabled", matchIfMissing = true)
    static class AsyncEnableConfiguration {
    }
}
