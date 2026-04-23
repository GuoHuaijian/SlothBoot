package com.sloth.boot.common.util;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 重试工具类
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class RetryUtil {

    /**
     * 执行带重试的任务
     *
     * @param task      任务
     * @param maxRetries 最大重试次数
     * @param delay     重试间隔
     * @param <T>       返回类型
     * @return 任务结果
     */
    public static <T> T executeWithRetry(Supplier<T> task, int maxRetries, Duration delay) {
        return executeWithRetry(task, maxRetries, delay, null);
    }

    /**
     * 执行带重试的任务
     *
     * @param task      任务
     * @param maxRetries 最大重试次数
     * @param delay     重试间隔
     * @param exceptionClass 需要重试的异常类型
     * @param <T>       返回类型
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
     * 执行带重试的任务（使用线程池）
     *
     * @param task      任务
     * @param maxRetries 最大重试次数
     * @param delay     重试间隔
     * @param <T>       返回类型
     * @return 任务结果
     */
    public static <T> T executeWithRetryAsync(Supplier<T> task, int maxRetries, Duration delay) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            return executeWithRetry(() -> {
                try {
                    return executor.submit(task::get).get(delay.toMillis(), TimeUnit.MILLISECONDS);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, maxRetries, delay);
        } finally {
            executor.shutdown();
        }
    }
}
