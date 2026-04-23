package com.sloth.boot.starter.monitor.alarm;

import com.sloth.boot.common.context.TraceContext;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 告警消息对象。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
public class AlarmMessage {

    /**
     * 告警标题。
     */
    private String title;

    /**
     * 告警内容。
     */
    private String content;

    /**
     * 告警级别。
     */
    private String level = "WARN";

    /**
     * 告警时间。
     */
    private LocalDateTime time = LocalDateTime.now();

    /**
     * Trace ID。
     */
    private String traceId = TraceContext.getTraceId();

    /**
     * 服务名。
     */
    private String serviceName;

    /**
     * 服务 IP。
     */
    private String ip;
}
