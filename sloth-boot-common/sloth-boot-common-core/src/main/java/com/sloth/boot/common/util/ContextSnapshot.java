package com.sloth.boot.common.util;

import com.sloth.boot.common.context.TraceContext;
import com.sloth.boot.common.context.UserContext;
import org.slf4j.MDC;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 上下文快照工具类。
 * <p>
 * 用于在线程切换（线程池、异步任务、消息消费等）时捕获和恢复上下文信息，
 * 包括 {@link TraceContext}、{@link UserContext}、SLF4J MDC 和 {@link RequestContextHolder}。
 * <p>
 * 使用示例：
 * <pre>
 * // 在线程池中使用
 * ContextSnapshot snapshot = ContextSnapshot.capture();
 * executor.submit(snapshot.decorate(() -&gt; {
 *     // 此处可正常访问 TraceContext 和 UserContext
 *     String traceId = TraceContext.getTraceId();
 *     Long userId = UserContext.getUserId();
 * }));
 *
 * // 在 TaskDecorator 中使用
 * public class MyTaskDecorator implements TaskDecorator {
 *     public Runnable decorate(Runnable runnable) {
 *         return ContextSnapshot.capture().decorate(runnable);
 *     }
 * }
 * </pre>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class ContextSnapshot {

    private static final boolean MDC_AVAILABLE;
    private static final boolean REQUESTContextHolder_AVAILABLE;

    static {
        MDC_AVAILABLE = isClassPresent("org.slf4j.MDC");
        REQUESTContextHolder_AVAILABLE = isClassPresent("org.springframework.web.context.request.RequestContextHolder");
    }

    private final TraceContext.TraceInfo traceInfo;
    private final UserContext.UserInfo userInfo;
    private final Map<String, String> mdcContext;
    private final RequestAttributes requestAttributes;

    private ContextSnapshot(TraceContext.TraceInfo traceInfo, UserContext.UserInfo userInfo,
                            Map<String, String> mdcContext, RequestAttributes requestAttributes) {
        this.traceInfo = traceInfo;
        this.userInfo = userInfo;
        this.mdcContext = mdcContext;
        this.requestAttributes = requestAttributes;
    }

    /**
     * 捕获当前线程的上下文快照
     *
     * @return 上下文快照
     */
    public static ContextSnapshot capture() {
        TraceContext.TraceInfo traceInfo = TraceContext.get();
        UserContext.UserInfo userInfo = UserContext.get();
        Map<String, String> mdcContext = MDC_AVAILABLE ? MDC.getCopyOfContextMap() : null;
        RequestAttributes requestAttributes = REQUESTContextHolder_AVAILABLE
            ? RequestContextHolder.getRequestAttributes() : null;
        return new ContextSnapshot(traceInfo, userInfo, mdcContext, requestAttributes);
    }

    /**
     * 在当前线程应用快照中的上下文
     */
    public void apply() {
        if (traceInfo != null) {
            TraceContext.set(traceInfo);
        }
        if (userInfo != null) {
            UserContext.set(userInfo);
        }
        if (MDC_AVAILABLE) {
            if (mdcContext != null) {
                MDC.setContextMap(mdcContext);
            } else {
                MDC.clear();
            }
        }
        if (REQUESTContextHolder_AVAILABLE && requestAttributes != null) {
            RequestContextHolder.setRequestAttributes(requestAttributes);
        }
    }

    /**
     * 清除当前线程的上下文
     */
    public void clear() {
        TraceContext.clear();
        UserContext.clear();
        if (MDC_AVAILABLE) {
            MDC.clear();
        }
        if (REQUESTContextHolder_AVAILABLE) {
            RequestContextHolder.resetRequestAttributes();
        }
    }

    /**
     * 包装 Runnable，在执行前应用上下文，执行后清除
     *
     * @param runnable 原始 Runnable
     * @return 包装后的 Runnable
     */
    public Runnable decorate(Runnable runnable) {
        return () -> {
            apply();
            try {
                runnable.run();
            } finally {
                clear();
            }
        };
    }

    /**
     * 包装 Callable，在执行前应用上下文，执行后清除
     *
     * @param callable 原始 Callable
     * @param <T>      返回类型
     * @return 包装后的 Callable
     */
    public <T> Callable<T> decorate(Callable<T> callable) {
        return () -> {
            apply();
            try {
                return callable.call();
            } finally {
                clear();
            }
        };
    }

    private static boolean isClassPresent(String className) {
        try {
            Class.forName(className, false, ContextSnapshot.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
