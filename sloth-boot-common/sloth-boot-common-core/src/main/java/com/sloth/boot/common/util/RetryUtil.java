package com.sloth.boot.common.util;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * 重试工具类
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class RetryUtil {

    private RetryUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 共享的异步重试线程池
     */
    private static final ExecutorService ASYNC_EXECUTOR = new ThreadPoolExecutor(
            0, 4, 60L, TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            new RetryThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    /**
     * 执行带重试的任务
     *
     * @param task       任务
     * @param maxRetries 最大重试次数
     * @param delay      重试间隔
     * @param <T>        返回类型
     * @return 任务结果
     */
    public static <T> T executeWithRetry(Supplier<T> task, int maxRetries, Duration delay) {
        return executeWithRetry(task, maxRetries, delay, null);
    }

    /**
     * 执行带重试的任务
     *
     * @param task          任务
     * @param maxRetries    最大重试次数
     * @param delay         重试间隔
     * @param exceptionClass 需要重试的异常类型
     * @param <T>           返回类型
     * @return 任务结果
     */
    public static <T> T executeWithRetry(Supplier<T> task, int maxRetries, Duration delay, Class<? extends Exception> exceptionClass) {
        int retryCount = 0;
        while (true) {
            try {
                return task.get();
            } catch (Exception e) {
                if (exceptionClass != null && !exceptionClass.isInstance(e)) {
                    throw e;
                }
                retryCount++;
                if (retryCount > maxRetries) {
                    throw e;
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(delay.toMillis());
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("重试被中断", ie);
                }
            }
        }
    }

    /**
     * 执行带重试的任务（异步执行，使用共享线程池）
     *
     * @param task       任务
     * @param maxRetries 最大重试次数
     * @param delay      重试间隔
     * @param <T>        返回类型
     * @return 任务结果
     */
    public static <T> T executeWithRetryAsync(Supplier<T> task, int maxRetries, Duration delay) {
        return executeWithRetry(() -> {
            try {
                return ASYNC_EXECUTOR.submit(task::get).get(delay.toMillis(), TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, maxRetries, delay);
    }

    /**
     * 执行带重试的任务（异步执行，使用自定义线程池）
     *
     * @param task       任务
     * @param maxRetries 最大重试次数
     * @param delay      重试间隔
     * @param executor   自定义线程池
     * @param <T>        返回类型
     * @return 任务结果
     */
    public static <T> T executeWithRetryAsync(Supplier<T> task, int maxRetries, Duration delay, ExecutorService executor) {
        return executeWithRetry(() -> {
            try {
                return executor.submit(task::get).get(delay.toMillis(), TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, maxRetries, delay);
    }

    /**
     * 重试线程工厂
     */
    private static class RetryThreadFactory implements ThreadFactory {
        private final AtomicInteger counter = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "retry-async-" + counter.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        }
    }
}
