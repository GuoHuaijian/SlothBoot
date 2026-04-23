package com.sloth.boot.common.event;

import com.sloth.boot.common.context.TraceContext;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

/**
 * 基础事件类
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class BaseEvent extends ApplicationEvent {

    /**
     * 追踪ID
     */
    private final String traceId;

    /**
     * 事件时间
     */
    private final LocalDateTime eventTime;

    /**
     * 事件来源
     */
    private final String source;

    /**
     * 构造函数
     *
     * @param source 事件来源
     */
    public BaseEvent(Object source) {
        super(source);
        this.traceId = TraceContext.getTraceId();
        this.eventTime = LocalDateTime.now();
        this.source = source.toString();
    }

    /**
     * 获取追踪ID
     *
     * @return 追踪ID
     */
    public String getTraceId() {
        return traceId;
    }

    /**
     * 获取事件时间
     *
     * @return 事件时间
     */
    public LocalDateTime getEventTime() {
        return eventTime;
    }

    /**
     * 获取事件来源
     *
     * @return 事件来源
     */
    public String getSource() {
        return source;
    }
}
