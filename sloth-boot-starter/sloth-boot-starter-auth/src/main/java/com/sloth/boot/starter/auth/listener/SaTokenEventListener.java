package com.sloth.boot.starter.auth.listener;

import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import com.sloth.boot.starter.auth.enums.DeviceStrategy;
import com.sloth.boot.starter.auth.event.LoginEvent;
import com.sloth.boot.starter.auth.config.AuthProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Sa-Token 事件监听桥接器。
 * <p>
 * 将 Sa-Token 的登录/登出/踢人事件桥接为 Spring ApplicationEvent， 供业务方监听实现登录日志持久化等操作。
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

    /**
     * 构造事件监听器。
     *
     * @param eventPublisher Spring 事件发布器
     * @param authProperties 认证配置属性
     */
    public SaTokenEventListener(ApplicationEventPublisher eventPublisher, AuthProperties authProperties) {
        this.eventPublisher = eventPublisher;
        this.authProperties = authProperties;
    }

    /**
     * 用户登录事件处理：发布 Spring 事件并执行多设备登录策略。
     *
     * @param loginType  账号类型
     * @param loginId    用户 ID
     * @param tokenValue Token 值
     * @param loginModel 登录参数
     */
    @Override
    public void doLogin(String loginType, Object loginId, String tokenValue, SaLoginParameter loginModel) {
        String device = loginModel != null ? loginModel.getDevice() : null;
        log.debug("[Auth] 用户登录: loginId={}, device={}", loginId, device);
        publishEvent(loginId, "login", device);

        // 多设备登录策略
        DeviceStrategy strategy = authProperties.getDeviceStrategy();
        if (strategy == DeviceStrategy.REPLACED) {
            cn.dev33.satoken.stp.StpUtil.kickout(loginId, device);
        } else if (strategy == DeviceStrategy.PROHIBIT) {
            // 已有设备登录时，踢掉新登录的设备
            try {
                cn.dev33.satoken.stp.StpUtil.kickout(loginId, device);
            } catch (Exception e) {
                log.debug("[Auth] 踢出旧会话失败(可能无旧会话): loginId={}, device={}", loginId, device, e);
            }
        }
    }

    /**
     * 用户登出事件处理：发布 Spring 事件。
     *
     * @param loginType 账号类型
     * @param loginId   用户 ID
     * @param device    设备标识
     */
    @Override
    public void doLogout(String loginType, Object loginId, String device) {
        log.debug("[Auth] 用户登出: loginId={}, device={}", loginId, device);
        publishEvent(loginId, "logout", device);
    }

    /**
     * 用户被踢事件处理：发布 Spring 事件。
     *
     * @param loginType 账号类型
     * @param loginId   用户 ID
     * @param device    设备标识
     */
    @Override
    public void doKickout(String loginType, Object loginId, String device) {
        log.debug("[Auth] 用户被踢: loginId={}, device={}", loginId, device);
        publishEvent(loginId, "kickout", device);
    }

    /**
     * 用户被顶替事件处理：发布 Spring 事件。
     *
     * @param loginType 账号类型
     * @param loginId   用户 ID
     * @param device    设备标识
     */
    @Override
    public void doReplaced(String loginType, Object loginId, String device) {
        log.debug("[Auth] 用户被顶替: loginId={}, device={}", loginId, device);
        publishEvent(loginId, "replaced", device);
    }

    /**
     * 账号封禁事件处理。
     *
     * @param loginType   账号类型
     * @param loginId     用户 ID
     * @param service     封禁服务
     * @param level       封禁等级
     * @param disableTime 封禁时长（秒）
     */
    @Override
    public void doDisable(String loginType, Object loginId, String service, int level, long disableTime) {
        log.debug("[Auth] 账号被封禁: loginId={}, level={}", loginId, level);
    }

    /**
     * 账号解封事件处理。
     *
     * @param loginType 账号类型
     * @param loginId   用户 ID
     * @param service   封禁服务
     */
    @Override
    public void doUntieDisable(String loginType, Object loginId, String service) {
        log.debug("[Auth] 账号解封: loginId={}", loginId);
    }

    /**
     * 安全模式开启事件处理（暂不处理）。
     *
     * @param loginType 账号类型
     * @param safeToken 安全 Token
     * @param device    设备标识
     * @param timeout   超时时间（秒）
     */
    @Override
    public void doOpenSafe(String loginType, String safeToken, String device, long timeout) {
        // 安全模式开启，暂不处理
    }

    /**
     * 安全模式关闭事件处理（暂不处理）。
     *
     * @param loginType 账号类型
     * @param safeToken 安全 Token
     * @param device    设备标识
     */
    @Override
    public void doCloseSafe(String loginType, String safeToken, String device) {
        // 安全模式关闭，暂不处理
    }

    /**
     * Session 创建事件处理（暂不处理）。
     *
     * @param sessionId Session ID
     */
    @Override
    public void doCreateSession(String sessionId) {
        // Session 创建，暂不处理
    }

    /**
     * Session 注销事件处理（暂不处理）。
     *
     * @param sessionId Session ID
     */
    @Override
    public void doLogoutSession(String sessionId) {
        // Session 注销，暂不处理
    }

    /**
     * Token 续期事件处理（暂不处理）。
     *
     * @param loginType 账号类型
     * @param loginId   用户 ID
     * @param device    设备标识
     * @param timeout   新的超时时间（秒）
     */
    @Override
    public void doRenewTimeout(String loginType, Object loginId, String device, long timeout) {
        // Token 续期，暂不处理
    }

    /**
     * 发布登录事件到 Spring 事件总线。
     *
     * @param loginId   用户 ID
     * @param loginType 登录类型
     * @param device    设备标识
     */
    private void publishEvent(Object loginId, String loginType, String device) {
        Long userId = null;
        try {
            userId = loginId instanceof Long ? (Long) loginId : Long.parseLong(loginId.toString());
        } catch (NumberFormatException e) {
            log.debug("[Auth] loginId 无法转换为 Long: {}", loginId, e);
        }
        String loginIp = null;
        String userAgent = null;
        try {
            cn.dev33.satoken.context.model.SaRequest request = cn.dev33.satoken.context.SaHolder.getRequest();
            loginIp = request.getHeader("X-Forwarded-For");
            if (loginIp == null || loginIp.isBlank() || "unknown".equalsIgnoreCase(loginIp)) {
                loginIp = request.getHeader("X-Real-IP");
            }
            if (loginIp == null || loginIp.isBlank() || "unknown".equalsIgnoreCase(loginIp)) {
                loginIp = request.getHost();
            }
            if (loginIp != null && loginIp.contains(",")) {
                loginIp = loginIp.split(",")[0].trim();
            }
            userAgent = request.getHeader("User-Agent");
        } catch (Exception e) {
            log.debug("[Auth] 获取请求信息失败(非 Web 环境)", e);
        }
        LoginEvent event = new LoginEvent(userId, loginType, loginIp, userAgent, device);
        eventPublisher.publishEvent(event);
    }
}
