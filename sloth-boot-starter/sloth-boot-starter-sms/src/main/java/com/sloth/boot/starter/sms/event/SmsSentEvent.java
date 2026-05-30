package com.sloth.boot.starter.sms.event;

import com.sloth.boot.common.event.BaseEvent;
import lombok.Getter;

/**
 * 短信发送事件。
 * <p>
 * 在短信发送完成（成功或失败）时发布，用于审计和监控。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
public class SmsSentEvent extends BaseEvent {

    private final String phone;
    private final String templateCode;
    private final boolean success;
    private final String msgId;
    private final String errorMessage;

    public SmsSentEvent(Object source, String phone, String templateCode,
                        boolean success, String msgId, String errorMessage) {
        super(source);
        this.phone = phone;
        this.templateCode = templateCode;
        this.success = success;
        this.msgId = msgId;
        this.errorMessage = errorMessage;
    }
}
