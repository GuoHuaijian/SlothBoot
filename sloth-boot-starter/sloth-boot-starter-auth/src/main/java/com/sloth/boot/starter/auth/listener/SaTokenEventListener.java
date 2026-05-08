package com.sloth.boot.starter.auth.listener;

import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.stp.SaLoginModel;
import com.sloth.boot.starter.auth.enums.DeviceStrategy;
import com.sloth.boot.starter.auth.event.LoginEvent;
import com.sloth.boot.starter.auth.properties.AuthProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Sa-Token 事件监听桥接器。
 * <p>
 * 将 Sa-Token 的登录/登出/踢人事件桥接为 Spring ApplicationEvent，
 * 供业务方监听实现登录日志持久化等操作。
 * <p>
 * 同时实现多设备登录策略控制。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
public class SaTokenEventListener implements SaTokenListener {

    private final ApplicationEventPublisher eventPublisher;
    private final AuthProperties authProperties;

    public SaTokenEventListener(ApplicationEventPublisher eventPublisher, AuthProperties authProperties) {
        this.eventPublisher = eventPublisher;
        this.authProperties = authProperties;
    }

    @Override
    public void doLogin(String loginType, Object loginId, String tokenValue, SaLoginModel loginModel) {
        String device = loginModel != null ? loginModel.getDevice() : null;
        log.debug("[Auth] 用户登录: loginId={}, device={}", loginId, device);
        publishEvent(loginId, "login", device);

        // 多设备登录策略
        DeviceStrategy strategy = authProperties.getDeviceStrategy();
        if (strategy == DeviceStrategy.REPLACED) {
            cn.dev33.satoken.stp.StpUtil.kickout(loginId, device);
        }
    }

    @Override
    public void doLogout(String loginType, Object loginId, String device) {
        log.debug("[Auth] 用户登出: loginId={}, device={}", loginId, device);
        publishEvent(loginId, "logout", device);
    }

    @Override
    public void doKickout(String loginType, Object loginId, String device) {
        log.debug("[Auth] 用户被踢: loginId={}, device={}", loginId, device);
        publishEvent(loginId, "kickout", device);
    }

    @Override
    public void doReplaced(String loginType, Object loginId, String device) {
        log.debug("[Auth] 用户被顶替: loginId={}, device={}", loginId, device);
        publishEvent(loginId, "replaced", device);
    }

    @Override
    public void doDisable(String loginType, Object loginId, String service, int level, long disableTime) {
        log.debug("[Auth] 账号被封禁: loginId={}, level={}", loginId, level);
    }

    @Override
    public void doUntieDisable(String loginType, Object loginId, String service) {
        log.debug("[Auth] 账号解封: loginId={}", loginId);
    }

    @Override
    public void doOpenSafe(String loginType, String safeToken, String device, long timeout) {
        // 安全模式开启，暂不处理
    }

    @Override
    public void doCloseSafe(String loginType, String safeToken, String device) {
        // 安全模式关闭，暂不处理
    }

    @Override
    public void doCreateSession(String sessionId) {
        // Session 创建，暂不处理
    }

    @Override
    public void doLogoutSession(String sessionId) {
        // Session 注销，暂不处理
    }

    @Override
    public void doRenewTimeout(String loginType, Object loginId, long timeout) {
        // Token 续期，暂不处理
    }

    private void publishEvent(Object loginId, String loginType, String device) {
        Long userId = null;
        try {
            userId = loginId instanceof Long ? (Long) loginId : Long.parseLong(loginId.toString());
        } catch (NumberFormatException ignored) {
        }
        LoginEvent event = new LoginEvent(this, userId, loginType, null, null, device);
        eventPublisher.publishEvent(event);
    }
}
