package com.sloth.boot.starter.thread.monitor;

import com.sloth.boot.starter.thread.core.ThreadPoolManager;
import com.sloth.boot.starter.thread.core.ThreadPoolRegistry;
import com.sloth.boot.starter.thread.core.VisibleThreadPoolExecutor;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 线程池端点。
 * <p>
 * 提供全部线程池运行时快照、单个线程池详细信息、以及动态调整线程池参数的能力。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Endpoint(id = "threadPools")
public class ThreadPoolEndpoint {

    private final ThreadPoolRegistry threadPoolRegistry;
    private final ThreadPoolManager threadPoolManager;

    /**
     * 构造函数。
     *
     * @param threadPoolRegistry 线程池注册表
     * @param threadPoolManager  线程池管理器
     */
    public ThreadPoolEndpoint(ThreadPoolRegistry threadPoolRegistry, ThreadPoolManager threadPoolManager) {
        this.threadPoolRegistry = threadPoolRegistry;
        this.threadPoolManager = threadPoolManager;
    }

    /**
     * 读取全部线程池状态。
     *
     * @return 线程池状态
     */
    @ReadOperation
    public Map<String, Map<String, Object>> pools() {
        return threadPoolRegistry.getAllSnapshots();
    }

    /**
     * 读取单个线程池详细信息。
     *
     * @param poolName 线程池名称
     * @return 线程池快照信息，不存在时返回错误提示
     */
    @ReadOperation
    public Map<String, Object> pool(@Selector String poolName) {
        VisibleThreadPoolExecutor executor = threadPoolRegistry.getPool(poolName);
        if (executor == null) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("error", "线程池不存在: " + poolName);
            return error;
        }
        return executor.snapshot();
    }

    /**
     * 动态调整线程池参数。
     *
     * @param poolName 线程池名称
     * @param coreSize 新的核心线程数（可选）
     * @param maxSize  新的最大线程数（可选）
     * @return 操作结果
     */
    @WriteOperation
    public Map<String, Object> updatePool(String poolName, Integer coreSize, Integer maxSize) {
        Map<String, Object> result = new LinkedHashMap<>();
        VisibleThreadPoolExecutor executor = threadPoolRegistry.getPool(poolName);
        if (executor == null) {
            result.put("success", false);
            result.put("message", "线程池不存在: " + poolName);
            return result;
        }
        int newCore = coreSize != null ? coreSize : executor.getCorePoolSize();
        int newMax = maxSize != null ? maxSize : executor.getMaximumPoolSize();
        threadPoolManager.updatePoolSize(poolName, newCore, newMax);
        result.put("success", true);
        result.put("poolName", poolName);
        result.put("corePoolSize", newCore);
        result.put("maximumPoolSize", newMax);
        return result;
    }
}
