package com.sloth.boot.starter.threadpool.core;

/**
 * 线程池统一访问入口。
 * <p>
 * 提供内置线程池的常量名和类型化获取方法，避免硬编码字符串。
 * 业务代码通过此类获取线程池，而非直接操作 {@link ThreadPoolRegistry}。
 *
 * <pre>
 * // 推荐用法
 * Executor executor = ThreadPools.httpClient();
 *
 * // 避免的用法
 * Executor executor = threadPoolRegistry.getPool("http-client");
 * </pre>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class ThreadPools {

    private ThreadPools() {
        throw new UnsupportedOperationException("Utility class");
    }

    // ==================== 内置线程池名称常量 ====================

    /** 默认异步线程池 */
    public static final String DEFAULT = "default";

    /** 定时任务线程池 */
    public static final String SCHEDULED = "scheduled";

    /** HTTP 客户端线程池 */
    public static final String HTTP_CLIENT = "http-client";

    /** 消息队列消费线程池 */
    public static final String MQ_CONSUMER = "mq-consumer";

    /** 数据同步线程池 */
    public static final String DATA_SYNC = "data-sync";

    // ==================== 静态访问方法 ====================

    private static volatile ThreadPoolRegistry registry;

    /**
     * 注册中心初始化（由自动配置调用）。
     *
     * @param registry 线程池注册表
     */
    public static void init(ThreadPoolRegistry registry) {
        ThreadPools.registry = registry;
    }

    /**
     * 获取默认异步线程池。
     *
     * @return 可观测线程池执行器
     */
    public static VisibleThreadPoolExecutor defaultPool() {
        return getPool(DEFAULT);
    }

    /**
     * 获取定时任务线程池。
     *
     * @return 定时任务线程池
     */
    public static java.util.concurrent.ScheduledThreadPoolExecutor scheduledPool() {
        return registry != null ? registry.getScheduledPool(SCHEDULED) : null;
    }

    /**
     * 获取 HTTP 客户端线程池。
     *
     * @return 可观测线程池执行器
     */
    public static VisibleThreadPoolExecutor httpClient() {
        return getPool(HTTP_CLIENT);
    }

    /**
     * 获取消息队列消费线程池。
     *
     * @return 可观测线程池执行器
     */
    public static VisibleThreadPoolExecutor mqConsumer() {
        return getPool(MQ_CONSUMER);
    }

    /**
     * 获取数据同步线程池。
     *
     * @return 可观测线程池执行器
     */
    public static VisibleThreadPoolExecutor dataSync() {
        return getPool(DATA_SYNC);
    }

    /**
     * 根据名称获取线程池。
     *
     * @param name 线程池名称（使用 {@link #DEFAULT} 等常量）
     * @return 可观测线程池执行器，不存在时返回 null
     */
    public static VisibleThreadPoolExecutor getPool(String name) {
        return registry != null ? registry.getPool(name) : null;
    }

    /**
     * 判断指定名称的线程池是否存在。
     *
     * @param name 线程池名称
     * @return 是否存在
     */
    public static boolean exists(String name) {
        return registry != null && registry.getPool(name) != null;
    }
}
