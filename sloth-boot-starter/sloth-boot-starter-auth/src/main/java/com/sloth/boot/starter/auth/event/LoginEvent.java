package com.sloth.boot.starter.auth.event;

/**
 * 登录/登出事件。
 * <p>
 * 业务方可监听此事件实现登录日志持久化。
 * <pre>
 * &#64;EventListener
 * public void onLogin(LoginEvent event) {
 *     loginLogService.save(event);
 * }
 * </pre>
 *
 * @param userId    用户 ID
 * @param loginType 登录类型：login / logout / kickout / forced_logout
 * @param loginIp   登录 IP
 * @param userAgent User-Agent
 * @param device    设备标识
 * @author sloth-boot
 * @since 1.0.0
 */
public record LoginEvent(Long userId, String loginType, String loginIp, String userAgent, String device) {
}
