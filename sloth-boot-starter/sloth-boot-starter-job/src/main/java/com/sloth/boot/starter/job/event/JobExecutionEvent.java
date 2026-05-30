package com.sloth.boot.starter.job.event;

import com.sloth.boot.common.event.BaseEvent;
import lombok.Getter;

/**
 * Job 执行事件。
 * <p>
 * 在定时任务执行完成时发布，用于监控和审计。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
public class JobExecutionEvent extends BaseEvent {

    private final String handlerName;
    private final long costTimeMs;
    private final boolean success;
    private final String errorMessage;

    public JobExecutionEvent(Object source, String handlerName, long costTimeMs,
                             boolean success, String errorMessage) {
        super(source);
        this.handlerName = handlerName;
        this.costTimeMs = costTimeMs;
        this.success = success;
        this.errorMessage = errorMessage;
    }
}
