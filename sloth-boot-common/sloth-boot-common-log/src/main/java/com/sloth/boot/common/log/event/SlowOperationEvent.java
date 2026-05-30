package com.sloth.boot.common.log.event;

import com.sloth.boot.common.event.BaseEvent;
import lombok.Getter;

/**
 * 慢操作事件。
 * <p>
 * 各模块在检测到慢操作时发布此事件，统一慢操作日志格式。
 * 监听方可用于告警、指标采集和日志持久化。
 * <pre>
 * // 发布示例
 * eventPublisher.publishEvent(new SlowOperationEvent(this,
 *     "SQL", "SELECT * FROM user", 1500, 500));
 * </pre>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
public class SlowOperationEvent extends BaseEvent {

    /**
     * 操作类型：SQL / ES_QUERY / HTTP / REDIS / MQ
     */
    private final String operationType;

    /**
     * 操作详情（SQL 语句、查询 DSL、请求 URL 等）
     */
    private final String detail;

    /**
     * 实际耗时（毫秒）
     */
    private final long costTimeMs;

    /**
     * 慢操作阈值（毫秒）
     */
    private final long thresholdMs;

    /**
     * 额外上下文信息（如方法名、表名、索引名等）
     */
    private final String context;

    public SlowOperationEvent(Object source, String operationType, String detail,
                              long costTimeMs, long thresholdMs) {
        this(source, operationType, detail, costTimeMs, thresholdMs, null);
    }

    public SlowOperationEvent(Object source, String operationType, String detail,
                              long costTimeMs, long thresholdMs, String context) {
        super(source);
        this.operationType = operationType;
        this.detail = detail;
        this.costTimeMs = costTimeMs;
        this.thresholdMs = thresholdMs;
        this.context = context;
    }
}
