package com.sloth.boot.starter.idempotent.event;

/**
 * 幂等拒绝事件。
 * <p>
 * 当重复请求被幂等机制拦截时发布此事件，用于审计和监控。
 *
 * @param key              幂等键
 * @param methodSignature  方法签名
 * @param mode             幂等模式
 * @param userId           用户 ID
 * @param clientIp         客户端 IP
 * @author sloth-boot
 * @since 1.0.0
 */
public record IdempotentRejectedEvent(String key, String methodSignature, String mode,
                                      Long userId, String clientIp) {
}
