package com.sloth.boot.common.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 事件发布器
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Component
public class EventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public EventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * 发布事件
     *
     * @param event 事件
     */
    public void publish(Object event) {
        applicationEventPublisher.publishEvent(event);
    }

    /**
     * 异步发布事件
     *
     * @param event 事件
     */
    public void publishAsync(Object event) {
        // 在实际应用中，可以使用 @Async 注解或消息队列实现异步发布
        applicationEventPublisher.publishEvent(event);
    }
}
