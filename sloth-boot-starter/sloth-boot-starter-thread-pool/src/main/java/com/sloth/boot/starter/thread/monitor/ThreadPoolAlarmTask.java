package com.sloth.boot.starter.thread.monitor;

import com.sloth.boot.starter.thread.core.ThreadPoolRegistry;
import com.sloth.boot.starter.thread.core.VisibleThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Map;

/**
 * 线程池队列使用率告警定时任务。
 * <p>
 * 定期检查所有注册的线程池，当队列使用率超过阈值时发布告警事件。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
public class ThreadPoolAlarmTask {

    private final ThreadPoolRegistry threadPoolRegistry;
    private final ApplicationEventPublisher eventPublisher;
    private final double alarmThreshold;

    public ThreadPoolAlarmTask(ThreadPoolRegistry threadPoolRegistry,
                                ApplicationEventPublisher eventPublisher,
                                double alarmThreshold) {
        this.threadPoolRegistry = threadPoolRegistry;
        this.eventPublisher = eventPublisher;
        this.alarmThreshold = alarmThreshold;
    }

    /**
     * 检查所有线程池队列使用率。
     */
    public void check() {
        Map<String, VisibleThreadPoolExecutor> pools = threadPoolRegistry.getAll();
        for (Map.Entry<String, VisibleThreadPoolExecutor> entry : pools.entrySet()) {
            String poolName = entry.getKey();
            VisibleThreadPoolExecutor executor = entry.getValue();
            VisibleThreadPoolExecutor.Snapshot snapshot = executor.snapshot();

            int queueCapacity = snapshot.getQueueCapacity();
            if (queueCapacity <= 0) {
                continue;
            }

            double usagePercent = (double) snapshot.getQueueSize() / queueCapacity * 100;
            if (usagePercent >= alarmThreshold) {
                log.warn("[ThreadPool] 线程池 {} 队列使用率告警: {:.1f}% ({}/{})",
                    poolName, usagePercent, snapshot.getQueueSize(), queueCapacity);

                ThreadPoolAlarmEvent event = new ThreadPoolAlarmEvent(
                    this, poolName, snapshot.getActiveCount(),
                    snapshot.getQueueSize(), queueCapacity, usagePercent,
                    snapshot.getCompletedTaskCount());
                eventPublisher.publishEvent(event);
            }
        }
    }
}
