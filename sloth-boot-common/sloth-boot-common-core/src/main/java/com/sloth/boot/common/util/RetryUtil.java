package com.sloth.boot.common.util;

import com.sloth.boot.common.exception.SystemException;

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
     * 共享的异步重试线程池。
     * 使用 JVM shutdown hook 确保应用关闭时优雅关闭线程池。
     */
    private static final ExecutorService ASYNC_EXECUTOR = new ThreadPoolExecutor(
        0, 4, 60L, TimeUnit.SECONDS,
        new SynchronousQueue<>(),
        new RetryThreadFactory(),
        new ThreadPoolExecutor.CallerRunsPolicy()
    );

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            ASYNC_EXECUTOR.shutdown();
            try {
                if (!ASYNC_EXECUTOR.awaitTermination(5, TimeUnit.SECONDS)) {
                    ASYNC_EXECUTOR.shutdownNow();
                }
            } catch (InterruptedException e) {
                ASYNC_EXECUTOR.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }, "retry-util-shutdown-hook"));
    }

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
     * @param task           任务
     * @param maxRetries     最大重试次数
     * @param delay          重试间隔
     * @param exceptionClass 需要重试的异常类型
     * @param <T>            返回类型
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
                    throw SystemException.of("Retry interrupted", ie);
                }
            }
        }
    }

    /**
     * 执行带重试的任务（异步执行，使用共享线程池）。
     *
     * @param task       任务
     * @param maxRetries 最大重试次数
     * @param delay      重试间隔
     * @param <T>        返回类型
     * @return 任务结果
     * @deprecated 该方法语义存在缺陷：Future.get(timeout) 的超时使用的是 delay（重试间隔），
     *     而非任务执行超时。如果任务执行时间超过 delay，会抛出 TimeoutException 并触发重试，
     *     这并非真正的"异步重试"语义。建议使用 {@link #executeWithRetry(Supplier, int, Duration)}
     *     配合外部线程池自行控制异步。将在未来版本移除。
     */
    @Deprecated
    public static <T> T executeWithRetryAsync(Supplier<T> task, int maxRetries, Duration delay) {
        Future<T> future = ASYNC_EXECUTOR.submit(task::get);
        try {
            return future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw SystemException.of("Async retry interrupted", e);
        } catch (ExecutionException e) {
            // 提取原始异常，进行重试逻辑
            Throwable cause = e.getCause();
            if (cause instanceof Exception ex) {
                return executeWithRetry(task, maxRetries, delay, (Class<? extends Exception>) cause.getClass());
            }
            throw SystemException.of("Async retry execution failed", e);
        }
    }

    /**
     * 执行带重试的任务（异步执行，使用自定义线程池）。
     *
     * @param task       任务
     * @param maxRetries 最大重试次数
     * @param delay      重试间隔
     * @param executor   自定义线程池
     * @param <T>        返回类型
     * @return 任务结果
     * @deprecated 该方法语义存在缺陷：Future.get(timeout) 的超时使用的是 delay（重试间隔），
     *     而非任务执行超时。建议使用 {@link #executeWithRetry(Supplier, int, Duration)} 配合
     *     外部线程池自行控制异步。将在未来版本移除。
     */
    @Deprecated
    public static <T> T executeWithRetryAsync(Supplier<T> task, int maxRetries, Duration delay, ExecutorService executor) {
        Future<T> future = executor.submit(task::get);
        try {
            return future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw SystemException.of("Async retry interrupted", e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof Exception ex) {
                return executeWithRetry(task, maxRetries, delay, (Class<? extends Exception>) cause.getClass());
            }
            throw SystemException.of("Async retry execution failed", e);
        }
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
