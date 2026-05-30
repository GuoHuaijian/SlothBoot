package com.sloth.boot.starter.threadpool.metrics;

import com.sloth.boot.starter.threadpool.core.ThreadPoolRegistry;
import com.sloth.boot.starter.threadpool.core.ThreadPoolSnapshot;
import com.sloth.boot.starter.threadpool.core.VisibleThreadPoolExecutor;
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

    public ThreadPoolAlarmTask(ThreadPoolRegistry threadPoolRegistry, ApplicationEventPublisher eventPublisher,
                               double alarmThreshold) {
        this.threadPoolRegistry = threadPoolRegistry;
        this.eventPublisher = eventPublisher;
        this.alarmThreshold = alarmThreshold;
    }

    /**
     * 检查所有线程池队列使用率。
     */
    public void check() {
        Map<String, VisibleThreadPoolExecutor> pools = threadPoolRegistry.getAllPools();
        for (VisibleThreadPoolExecutor executor : pools.values()) {
            ThreadPoolSnapshot snapshot = executor.snapshot();
            double usagePercent = snapshot.queueUsagePercent();

            if (usagePercent >= alarmThreshold) {
                log.warn("[ThreadPool] 线程池 {} 队列使用率告警: {}% ({}/{})", snapshot.poolName(),
                    String.format("%.1f", usagePercent), snapshot.queueSize(), snapshot.queueTotalCapacity());

                ThreadPoolAlarmEvent event = new ThreadPoolAlarmEvent(this, snapshot.poolName(),
                    snapshot.activeCount(), snapshot.queueSize(), snapshot.queueTotalCapacity(),
                    usagePercent, snapshot.completedTaskCount());
                eventPublisher.publishEvent(event);
            }
        }
    }
}
