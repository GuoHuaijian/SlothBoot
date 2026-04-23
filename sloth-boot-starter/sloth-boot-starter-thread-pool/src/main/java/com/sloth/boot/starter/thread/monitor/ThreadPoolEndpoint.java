package com.sloth.boot.starter.thread.monitor;

import com.sloth.boot.starter.thread.core.ThreadPoolRegistry;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import java.util.Map;

/**
 * 线程池端点。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Endpoint(id = "threadPools")
public class ThreadPoolEndpoint {

    private final ThreadPoolRegistry threadPoolRegistry;

    /**
     * 构造函数。
     *
     * @param threadPoolRegistry 线程池注册表
     */
    public ThreadPoolEndpoint(ThreadPoolRegistry threadPoolRegistry) {
        this.threadPoolRegistry = threadPoolRegistry;
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
}
