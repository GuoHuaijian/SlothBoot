package com.sloth.boot.starter.threadpool.metrics;

import com.sloth.boot.starter.threadpool.core.ThreadPoolSnapshot;
import com.sloth.boot.starter.threadpool.core.ThreadPools;
import com.sloth.boot.starter.threadpool.core.VisibleThreadPoolExecutor;

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

    /**
     * 读取全部线程池状态。
     *
     * @return 线程池状态
     */
    @ReadOperation
    public Map<String, ThreadPoolSnapshot> pools() {
        return ThreadPools.getAllSnapshots();
    }

    /**
     * 读取单个线程池详细信息。
     *
     * @param poolName 线程池名称
     * @return 线程池快照信息，不存在时返回 null
     */
    @ReadOperation
    public ThreadPoolSnapshot pool(@Selector String poolName) {
        VisibleThreadPoolExecutor executor = ThreadPools.getPool(poolName);
        if (executor == null) {
            return null;
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
        VisibleThreadPoolExecutor executor = ThreadPools.getPool(poolName);
        if (executor == null) {
            result.put("success", false);
            result.put("message", "线程池不存在: " + poolName);
            return result;
        }
        int newCore = coreSize != null ? coreSize : executor.getCorePoolSize();
        int newMax = maxSize != null ? maxSize : executor.getMaximumPoolSize();
        ThreadPools.resize(poolName, newCore, newMax);
        result.put("success", true);
        result.put("poolName", poolName);
        result.put("corePoolSize", newCore);
        result.put("maximumPoolSize", newMax);
        return result;
    }
}
