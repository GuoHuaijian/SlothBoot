package com.sloth.boot.starter.thread.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 异步异常处理器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    /**
     * 处理异步异常。
     *
     * @param throwable 异常
     * @param method    方法
     * @param objects   参数
     */
    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
        log.error("[ThreadPool] 异步任务执行异常, method={}, params={}",
            method.toGenericString(),
            Arrays.toString(objects),
            throwable);
    }
}
