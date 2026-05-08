package com.sloth.boot.starter.auth.service;

import java.util.List;

/**
 * 权限服务接口 (SPI)。
 * <p>
 * 业务系统需实现此接口，提供用户权限与角色数据。 未提供实现时，将使用 {@link DefaultPermissionService} 默认空实现。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public interface PermissionService {

    /**
     * 获取用户权限列表。
     *
     * @param userId 用户ID
     * @return 权限标识列表
     */
    List<String> getUserPermissions(Long userId);

    /**
     * 获取用户角色列表。
     *
     * @param userId 用户ID
     * @return 角色标识列表
     */
    List<String> getUserRoles(Long userId);
}
