package com.sloth.boot.starter.thread.config;

import com.sloth.boot.starter.thread.async.AsyncExceptionHandler;
import com.sloth.boot.starter.thread.core.ThreadPoolRegistry;
import com.sloth.boot.starter.thread.core.VisibleThreadPoolExecutor;
import com.sloth.boot.starter.thread.decorator.TtlTaskDecorator;
import com.sloth.boot.starter.thread.monitor.ThreadPoolEndpoint;
import com.sloth.boot.starter.thread.monitor.ThreadPoolMetrics;
import com.sloth.boot.starter.thread.reject.LogRejectedExecutionHandler;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
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
import java.util.concurrent.RejectedExecutionHandler;
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
@EnableAsync
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
    public ThreadPoolEndpoint threadPoolEndpoint(ThreadPoolRegistry threadPoolRegistry) {
        return new ThreadPoolEndpoint(threadPoolRegistry);
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
            return thread;
        };
    }
}
