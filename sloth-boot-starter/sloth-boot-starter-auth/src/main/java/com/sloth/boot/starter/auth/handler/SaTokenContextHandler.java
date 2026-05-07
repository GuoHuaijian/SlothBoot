package com.sloth.boot.starter.auth.handler;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.sloth.boot.common.context.UserContext;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Sa-Token 上下文处理器。
 * <p>
 * 负责在 Sa-Token 登录成功后同步用户信息到 {@link UserContext}，
 * 并提供权限/角色查询的默认实现。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Component
public class SaTokenContextHandler {

    /**
     * 同步 Sa-Token 登录信息到 UserContext。
     * <p>
     * 在拦截器或过滤器中调用此方法，将 Sa-Token 的登录态同步到框架的 UserContext。
     * 业务侧可继承此类并重写 {@link #buildUserInfo()} 来自定义用户信息填充逻辑。
     */
    public void syncToUserContext() {
        if (!StpUtil.isLogin()) {
            return;
        }
        UserContext.UserInfo userInfo = buildUserInfo();
        UserContext.set(userInfo);
    }

    /**
     * 构建用户信息。
     * <p>
     * 默认从 Sa-Token 的登录 ID 中提取用户 ID。
     * 业务侧应重写此方法，从数据库或缓存中加载完整的用户信息（角色、权限、租户等）。
     *
     * @return 用户信息
     */
    protected UserContext.UserInfo buildUserInfo() {
        UserContext.UserInfo userInfo = new UserContext.UserInfo();
        Object loginId = StpUtil.getLoginId();
        if (loginId instanceof Long longId) {
            userInfo.setUserId(longId);
        } else {
            try {
                userInfo.setUserId(Long.parseLong(loginId.toString()));
            } catch (NumberFormatException ignored) {
                // 非数字 ID，不设置 userId
            }
        }
        return userInfo;
    }

    /**
     * 清除用户上下文。
     */
    public void clearUserContext() {
        UserContext.clear();
    }
}
