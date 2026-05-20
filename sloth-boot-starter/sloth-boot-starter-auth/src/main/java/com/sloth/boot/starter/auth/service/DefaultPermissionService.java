package com.sloth.boot.starter.auth.service;

import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

/**
 * 权限服务默认空实现。
 * <p>
 * 当业务系统未提供 {@link PermissionService} 的自定义实现时使用。 所有方法返回空列表，功能不会生效。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
public class DefaultPermissionService implements PermissionService {

    public DefaultPermissionService() {
        log.warn("[Auth] 未检测到 PermissionService 自定义实现，已使用默认空实现。请实现 PermissionService 接口以启用 RBAC 权限校验。");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getUserPermissions(Long userId) {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getUserRoles(Long userId) {
        return Collections.emptyList();
    }
}
