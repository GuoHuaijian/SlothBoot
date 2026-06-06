package com.sloth.boot.common.decorator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 带日志的装饰器基类
 * <p>
 * 自动记录方法调用的入参、出参、耗时和异常信息。
 * 子类只需继承并传入目标对象即可获得日志能力。
 * <p>
 * 使用示例：
 * <pre>
 * public class UserServiceLogger extends LoggingDecorator&lt;UserService&gt; implements UserService {
 *
 *     public UserServiceLogger(UserService target) {
 *         super(target);
 *     }
 *
 *     public User findById(Long id) {
 *         return execute(() -&gt; target.findById(id), "findById", id);
 *     }
 * }
 * </pre>
 *
 * @param <T> 被装饰对象的类型
 * @author sloth-boot
 * @since 1.0.0
 */
public abstract class LoggingDecorator<T> extends AbstractDecorator<T> {

    protected final Logger log;
    private final long slowThresholdMs;

    protected LoggingDecorator(T target) {
        this(target, 1000L);
    }

    protected LoggingDecorator(T target, long slowThresholdMs) {
        super(target);
        this.log = LoggerFactory.getLogger(getClass());
        this.slowThresholdMs = slowThresholdMs;
    }

    @Override
    protected void before(String methodName, Object... args) {
        if (log.isDebugEnabled()) {
            log.debug("[{}] 入参: {}", methodName, formatArgs(args));
        }
    }

    @Override
    protected void after(String methodName, Object result, long elapsed) {
        if (log.isDebugEnabled()) {
            log.debug("[{}] 出参: {}, 耗时: {}ms", methodName, result, elapsed);
        } else if (elapsed > slowThresholdMs) {
            log.warn("[{}] 慢调用, 耗时: {}ms", methodName, elapsed);
        }
    }

    @Override
    protected void onError(String methodName, Throwable throwable, long elapsed) {
        log.error("[{}] 异常, 耗时: {}ms, 错误: {}", methodName, elapsed, throwable.getMessage(), throwable);
    }

    private String formatArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(args[i]);
        }
        sb.append("]");
        return sb.toString();
    }
}
