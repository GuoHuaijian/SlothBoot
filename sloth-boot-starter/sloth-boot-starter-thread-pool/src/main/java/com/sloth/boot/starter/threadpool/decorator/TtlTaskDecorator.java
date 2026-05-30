package com.sloth.boot.starter.threadpool.decorator;

import com.alibaba.ttl.TtlRunnable;
import com.sloth.boot.common.util.ContextSnapshot;
import org.springframework.core.task.TaskDecorator;

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
        ContextSnapshot snapshot = ContextSnapshot.capture();
        Runnable task = snapshot.decorate(runnable);
        return TtlRunnable.get(task);
    }
}
