package com.sloth.boot.common.log.event;

/**
 * 慢操作事件。
 * <p>
 * 各模块在检测到慢操作时发布此事件，统一慢操作日志格式。
 * 监听方可用于告警、指标采集和日志持久化。
 * <pre>
 * // 发布示例
 * eventPublisher.publishEvent(new SlowOperationEvent(
 *     "SQL", "SELECT * FROM user", 1500, 500));
 * </pre>
 *
 * @param operationType 操作类型：SQL / ES_QUERY / HTTP / REDIS / MQ
 * @param detail        操作详情（SQL 语句、查询 DSL、请求 URL 等）
 * @param costTimeMs    实际耗时（毫秒）
 * @param thresholdMs   慢操作阈值（毫秒）
 * @param context       额外上下文信息（如方法名、表名、索引名等）
 * @author sloth-boot
 * @since 1.0.0
 */
public record SlowOperationEvent(String operationType, String detail, long costTimeMs,
                                 long thresholdMs, String context) {

    /**
     * 创建无上下文信息的慢操作事件。
     *
     * @param operationType 操作类型
     * @param detail        操作详情
     * @param costTimeMs    实际耗时（毫秒）
     * @param thresholdMs   慢操作阈值（毫秒）
     * @return 慢操作事件
     */
    public static SlowOperationEvent of(String operationType, String detail, long costTimeMs, long thresholdMs) {
        return new SlowOperationEvent(operationType, detail, costTimeMs, thresholdMs, null);
    }
}
