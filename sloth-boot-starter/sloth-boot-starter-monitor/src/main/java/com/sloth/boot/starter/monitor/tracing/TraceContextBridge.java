package com.sloth.boot.starter.monitor.tracing;

import com.sloth.boot.common.context.TraceContext;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.handler.TracingObservationHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * TraceContext 与 Micrometer Tracing 的桥接器。
 * <p>
 * 实现 {@link TracingObservationHandler.SpanScope} 逻辑，将 Micrometer Tracing
 * 产生的 traceId 和 spanId 回写到自定义的 {@link TraceContext} 中，
 * 从而在异步线程和日志中保持统一的追踪标识。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
public class TraceContextBridge {

    private final Tracer tracer;

    public TraceContextBridge(Tracer tracer) {
        this.tracer = tracer;
        log.info("TraceContextBridge 已初始化，开始同步 TraceContext 与 Micrometer Tracing");
    }

    /**
     * 将当前 Micrometer Tracer 的 traceId 和 spanId 同步到 TraceContext。
     * <p>
     * 典型调用时机：在 Filter 或 Interceptor 中，于业务处理前调用此方法，
     * 确保 TraceContext 持有当前活跃 span 的追踪信息。
     */
    public void syncToTraceContext() {
        io.micrometer.tracing.Span currentSpan = tracer.currentSpan();
        if (currentSpan == null) {
            return;
        }
        TraceContext.TraceInfo traceInfo = new TraceContext.TraceInfo();
        traceInfo.setTraceId(currentSpan.context().traceId());
        traceInfo.setSpanId(currentSpan.context().spanId());
        TraceContext.set(traceInfo);
    }

    /**
     * 获取当前活跃 span 的 traceId。
     *
     * @return traceId，无活跃 span 时返回 null
     */
    public String getCurrentTraceId() {
        io.micrometer.tracing.Span currentSpan = tracer.currentSpan();
        return currentSpan != null ? currentSpan.context().traceId() : null;
    }

    /**
     * 获取当前活跃 span 的 spanId。
     *
     * @return spanId，无活跃 span 时返回 null
     */
    public String getCurrentSpanId() {
        io.micrometer.tracing.Span currentSpan = tracer.currentSpan();
        return currentSpan != null ? currentSpan.context().spanId() : null;
    }
}
