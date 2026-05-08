package com.sloth.boot.starter.auth.enums;

/**
 * 多设备登录策略枚举。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public enum DeviceStrategy {

    /**
     * 允许多设备同时登录（默认）。
     */
    ALLOW_MULTI,

    /**
     * 新设备登录时踢掉旧设备。
     */
    REPLACED,

    /**
     * 已有设备登录时拒绝新设备登录。
     */
    PROHIBIT
}
