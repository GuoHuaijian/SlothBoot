package com.sloth.boot.common.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 事件发布器
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Component
public class EventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;
    private static final Executor DEFAULT_ASYNC_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

    public EventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * 同步发布事件
     *
     * @param event 事件
     */
    public void publish(Object event) {
        applicationEventPublisher.publishEvent(event);
    }

    /**
     * 异步发布事件（使用默认虚拟线程执行器）
     *
     * @param event 事件
     */
    public void publishAsync(Object event) {
        publishAsync(event, DEFAULT_ASYNC_EXECUTOR);
    }

    /**
     * 异步发布事件（使用自定义执行器）
     *
     * @param event    事件
     * @param executor 自定义执行器
     */
    public void publishAsync(Object event, Executor executor) {
        executor.execute(() -> {
            try {
                applicationEventPublisher.publishEvent(event);
            } catch (Exception e) {
                log.error("[Event] Async event publish failed: {}", event.getClass().getSimpleName(), e);
            }
        });
    }
}
