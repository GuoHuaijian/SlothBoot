package com.sloth.boot.common.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 通用方法拦截器基类
 * <p>
 * 基于 AOP Alliance 的 {@link MethodInterceptor}，提供 before/after/onError 钩子方法。
 * 适用于自定义 AOP 切面场景。
 * <p>
 * 使用示例：
 * <pre>
 * public class PerformanceInterceptor extends AbstractMethodInterceptor {
 *
 *     protected void before(MethodInvocation invocation) {
 *         // 记录开始时间
 *     }
 *
 *     protected void after(MethodInvocation invocation, Object result, long elapsed) {
 *         // 记录耗时
 *     }
 * }
 * </pre>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public abstract class AbstractMethodInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        before(invocation);
        long start = System.currentTimeMillis();
        try {
            Object result = invocation.proceed();
            after(invocation, result, System.currentTimeMillis() - start);
            return result;
        } catch (Throwable e) {
            onError(invocation, e, System.currentTimeMillis() - start);
            throw e;
        }
    }

    /**
     * 方法执行前的钩子
     *
     * @param invocation 方法调用信息
     */
    protected void before(MethodInvocation invocation) {
        // 子类可重写
    }

    /**
     * 方法执行后的钩子
     *
     * @param invocation 方法调用信息
     * @param result     返回结果
     * @param elapsed    耗时（毫秒）
     */
    protected void after(MethodInvocation invocation, Object result, long elapsed) {
        // 子类可重写
    }

    /**
     * 方法执行异常的钩子
     *
     * @param invocation 方法调用信息
     * @param throwable  异常
     * @param elapsed    耗时（毫秒）
     */
    protected void onError(MethodInvocation invocation, Throwable throwable, long elapsed) {
        // 子类可重写
    }
}
