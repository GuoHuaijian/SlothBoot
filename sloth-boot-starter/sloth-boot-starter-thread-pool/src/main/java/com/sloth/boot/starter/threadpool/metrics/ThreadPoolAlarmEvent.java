package com.sloth.boot.starter.threadpool.metrics;

import com.sloth.boot.common.event.BaseEvent;
import lombok.Getter;

/**
 * 线程池告警事件。
 * <p>
 * 当线程池队列使用率超过阈值时发布。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
public class ThreadPoolAlarmEvent extends BaseEvent {

    /**
     * 线程池名称。
     */
    private final String poolName;

    /**
     * 活跃线程数。
     */
    private final int activeCount;

    /**
     * 队列当前大小。
     */
    private final int queueSize;

    /**
     * 队列容量。
     */
    private final int queueCapacity;

    /**
     * 队列使用率（百分比）。
     */
    private final double usagePercent;

    /**
     * 已完成任务数。
     */
    private final long completedTaskCount;

    public ThreadPoolAlarmEvent(Object source, String poolName, int activeCount,
                                int queueSize, int queueCapacity, double usagePercent,
                                long completedTaskCount) {
        super(source);
        this.poolName = poolName;
        this.activeCount = activeCount;
        this.queueSize = queueSize;
        this.queueCapacity = queueCapacity;
        this.usagePercent = usagePercent;
        this.completedTaskCount = completedTaskCount;
    }
}
