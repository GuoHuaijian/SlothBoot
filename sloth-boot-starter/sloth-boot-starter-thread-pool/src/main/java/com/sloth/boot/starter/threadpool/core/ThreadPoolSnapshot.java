package com.sloth.boot.starter.threadpool.core;

/**
 * 线程池运行时快照。
 *
 * @param poolName             线程池名称
 * @param corePoolSize         核心线程数
 * @param maximumPoolSize      最大线程数
 * @param poolSize             当前线程数
 * @param activeCount          活跃线程数
 * @param completedTaskCount   已完成任务数
 * @param taskCount            总任务数
 * @param queueSize            队列排队数
 * @param queueRemainingCapacity 队列剩余容量
 * @param rejectedCount        拒绝任务数
 * @param maxCostTime          最大耗时(ms)
 * @param avgCostTime          平均耗时(ms)
 * @author sloth-boot
 * @since 1.0.0
 */
public record ThreadPoolSnapshot(
    String poolName,
    int corePoolSize,
    int maximumPoolSize,
    int poolSize,
    int activeCount,
    long completedTaskCount,
    long taskCount,
    int queueSize,
    int queueRemainingCapacity,
    long rejectedCount,
    long maxCostTime,
    long avgCostTime
) {

    /**
     * 计算队列总容量。
     *
     * @return 队列总容量
     */
    public int queueTotalCapacity() {
        return queueSize + queueRemainingCapacity;
    }

    /**
     * 计算队列使用率百分比。
     *
     * @return 使用率（0-100），总容量为 0 时返回 0
     */
    public double queueUsagePercent() {
        int total = queueTotalCapacity();
        return total <= 0 ? 0.0 : (double) queueSize / total * 100;
    }
}
