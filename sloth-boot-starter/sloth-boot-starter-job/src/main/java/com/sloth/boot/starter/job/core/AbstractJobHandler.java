package com.sloth.boot.starter.job.core;

import com.sloth.boot.common.context.TraceContext;
import com.sloth.boot.common.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

/**
 * 作业处理器抽象基类。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
public abstract class AbstractJobHandler {

    /**
     * 执行作业。
     *
     * @throws Exception 异常
     */
    public final void execute() throws Exception {
        String traceId = TraceContext.generateTraceId();
        TraceContext.TraceInfo traceInfo = new TraceContext.TraceInfo();
        traceInfo.setTraceId(traceId);
        TraceContext.set(traceInfo);
        MDC.put("traceId", traceId);
        long startTime = System.currentTimeMillis();
        try {
            log.info("XXL-Job 开始执行, handler={}, traceId={}", getClass().getSimpleName(), traceId);
            doExecute();
            log.info("XXL-Job 执行成功, handler={}, traceId={}, cost={}ms",
                    getClass().getSimpleName(), traceId, System.currentTimeMillis() - startTime);
        } catch (Exception ex) {
            log.error("XXL-Job 执行失败, handler={}, traceId={}", getClass().getSimpleName(), traceId, ex);
            throw ex;
        } finally {
            MDC.remove("traceId");
            TraceContext.clear();
        }
    }

    /**
     * 子类实现具体逻辑。
     *
     * @throws Exception 异常
     */
    protected abstract void doExecute() throws Exception;
}
