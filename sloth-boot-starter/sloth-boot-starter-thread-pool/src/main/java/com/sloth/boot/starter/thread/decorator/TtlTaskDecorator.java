package com.sloth.boot.starter.thread.decorator;

import com.alibaba.ttl.TtlRunnable;
import com.sloth.boot.common.context.TraceContext;
import com.sloth.boot.common.context.UserContext;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Map;

/**
 * TTL 任务装饰器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class TtlTaskDecorator implements TaskDecorator {

    /**
     * 装饰任务，透传 TTL、MDC、用户上下文和请求上下文。
     *
     * @param runnable 原始任务
     * @return 装饰后的任务
     */
    @Override
    public Runnable decorate(Runnable runnable) {
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        UserContext.UserInfo userInfo = UserContext.get();
        TraceContext.TraceInfo traceInfo = TraceContext.get();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        Runnable task = () -> {
            Map<String, String> oldMdc = MDC.getCopyOfContextMap();
            UserContext.UserInfo oldUser = UserContext.get();
            TraceContext.TraceInfo oldTrace = TraceContext.get();
            RequestAttributes oldRequestAttributes = RequestContextHolder.getRequestAttributes();
            try {
                if (mdcContext != null) {
                    MDC.setContextMap(mdcContext);
                } else {
                    MDC.clear();
                }
                if (userInfo != null) {
                    UserContext.set(userInfo);
                }
                if (traceInfo != null) {
                    TraceContext.set(traceInfo);
                }
                if (requestAttributes != null) {
                    RequestContextHolder.setRequestAttributes(requestAttributes);
                }
                runnable.run();
            } finally {
                if (oldMdc != null) {
                    MDC.setContextMap(oldMdc);
                } else {
                    MDC.clear();
                }
                if (oldUser != null) {
                    UserContext.set(oldUser);
                } else {
                    UserContext.clear();
                }
                if (oldTrace != null) {
                    TraceContext.set(oldTrace);
                } else {
                    TraceContext.clear();
                }
                if (oldRequestAttributes != null) {
                    RequestContextHolder.setRequestAttributes(oldRequestAttributes);
                } else {
                    RequestContextHolder.resetRequestAttributes();
                }
            }
        };
        return TtlRunnable.get(task);
    }
}
