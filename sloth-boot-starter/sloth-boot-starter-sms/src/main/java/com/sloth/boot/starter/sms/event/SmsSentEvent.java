package com.sloth.boot.starter.sms.event;

/**
 * 短信发送事件。
 * <p>
 * 在短信发送完成（成功或失败）时发布，用于审计和监控。
 *
 * @param phone         手机号
 * @param templateCode  模板编码
 * @param success       是否发送成功
 * @param msgId         短信消息 ID
 * @param errorMessage  失败原因
 * @author sloth-boot
 * @since 1.0.0
 */
public record SmsSentEvent(String phone, String templateCode, boolean success,
                           String msgId, String errorMessage) {
}
