package com.sloth.boot.starter.threadpool.metrics;

/**
 * 线程池告警事件。
 * <p>
 * 当线程池队列使用率超过阈值时发布。
 *
 * @param poolName            线程池名称
 * @param activeCount         活跃线程数
 * @param queueSize           队列当前大小
 * @param queueCapacity       队列容量
 * @param usagePercent        队列使用率（百分比）
 * @param completedTaskCount  已完成任务数
 * @author sloth-boot
 * @since 1.0.0
 */
public record ThreadPoolAlarmEvent(String poolName, int activeCount, int queueSize,
                                   int queueCapacity, double usagePercent, long completedTaskCount) {
}
