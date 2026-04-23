package com.sloth.boot.starter.redis.delay;

/**
 * 延迟消息处理器。
 *
 * @param <T> 消息类型
 * @author sloth-boot
 * @since 1.0.0
 */
@FunctionalInterface
public interface DelayedMessageHandler<T> {

    /**
     * 处理延迟消息。
     *
     * @param message 消息体
     */
    void handle(T message);
}
