package com.sloth.boot.starter.idempotent.event;

import com.sloth.boot.common.event.BaseEvent;
import lombok.Getter;

/**
 * 幂等拒绝事件。
 * <p>
 * 当重复请求被幂等机制拦截时发布此事件，用于审计和监控。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
public class IdempotentRejectedEvent extends BaseEvent {

    private final String key;
    private final String methodSignature;
    private final String mode;
    private final Long userId;
    private final String clientIp;

    public IdempotentRejectedEvent(Object source, String key, String methodSignature,
                                   String mode, Long userId, String clientIp) {
        super(source);
        this.key = key;
        this.methodSignature = methodSignature;
        this.mode = mode;
        this.userId = userId;
        this.clientIp = clientIp;
    }
}
