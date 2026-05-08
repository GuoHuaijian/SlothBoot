package com.sloth.boot.common.decorator;

/**
 * 通用装饰器基类
 * <p>
 * 持有被装饰对象引用，子类通过重写 {@link #before}、{@link #after}、{@link #onError} 钩子方法
 * 实现增强逻辑（如日志、监控、降级等）。
 * <p>
 * 使用示例：
 * <pre>
 * public class LoggingServiceDecorator extends AbstractDecorator&lt;UserService&gt; {
 *
 *     public LoggingServiceDecorator(UserService target) {
 *         super(target);
 *     }
 *
 *     public User findById(Long id) {
 *         return execute(() -&gt; target.findById(id), "findById");
 *     }
 * }
 * </pre>
 *
 * @param <T> 被装饰对象的类型
 * @author sloth-boot
 * @since 1.0.0
 */
public abstract class AbstractDecorator<T> {

    /**
     * 被装饰的目标对象
     */
    protected final T target;

    /**
     * 构造装饰器
     *
     * @param target 被装饰的目标对象
     */
    protected AbstractDecorator(T target) {
        if (target == null) {
            throw new IllegalArgumentException("被装饰对象不能为 null");
        }
        this.target = target;
    }

    /**
     * 方法执行前的钩子
     *
     * @param methodName 方法名
     * @param args       参数
     */
    protected void before(String methodName, Object... args) {
        // 子类可重写
    }

    /**
     * 方法执行后的钩子
     *
     * @param methodName 方法名
     * @param result     返回结果
     * @param elapsed    耗时（毫秒）
     */
    protected void after(String methodName, Object result, long elapsed) {
        // 子类可重写
    }

    /**
     * 方法执行异常的钩子
     *
     * @param methodName 方法名
     * @param throwable  异常
     * @param elapsed    耗时（毫秒）
     */
    protected void onError(String methodName, Throwable throwable, long elapsed) {
        // 子类可重写
    }

    /**
     * 执行被装饰的方法（带 before/after/onError 生命周期）
     *
     * @param action     实际执行逻辑
     * @param methodName 方法名（用于日志和监控）
     * @param args       参数（传递给 before 钩子）
     * @param <R>        返回类型
     * @return 执行结果
     */
    protected <R> R execute(java.util.function.Supplier<R> action, String methodName, Object... args) {
        before(methodName, args);
        long start = System.currentTimeMillis();
        try {
            R result = action.get();
            after(methodName, result, System.currentTimeMillis() - start);
            return result;
        } catch (Throwable e) {
            onError(methodName, e, System.currentTimeMillis() - start);
            throw e;
        }
    }
}
