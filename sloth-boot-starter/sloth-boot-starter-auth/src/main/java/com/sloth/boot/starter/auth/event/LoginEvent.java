package com.sloth.boot.starter.auth.event;

import lombok.Getter;

import com.sloth.boot.common.event.BaseEvent;

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
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
public class LoginEvent extends BaseEvent {

    /**
     * 用户 ID。
     */
    private final Long userId;

    /**
     * 登录类型：login / logout / kickout / forced_logout。
     */
    private final String loginType;

    /**
     * 登录 IP。
     */
    private final String loginIp;

    /**
     * User-Agent。
     */
    private final String userAgent;

    /**
     * 设备标识。
     */
    private final String device;

    /**
     * 构造登录事件。
     *
     * @param source    事件源
     * @param userId    用户 ID
     * @param loginType 登录类型：login / logout / kickout / forced_logout
     * @param loginIp   登录 IP
     * @param userAgent User-Agent
     * @param device    设备标识
     */
    public LoginEvent(Object source, Long userId, String loginType, String loginIp, String userAgent, String device) {
        super(source);
        this.userId = userId;
        this.loginType = loginType;
        this.loginIp = loginIp;
        this.userAgent = userAgent;
        this.device = device;
    }
}
