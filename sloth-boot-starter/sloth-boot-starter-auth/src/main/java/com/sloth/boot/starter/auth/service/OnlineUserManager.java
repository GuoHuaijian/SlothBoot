package com.sloth.boot.starter.auth.service;

import java.util.List;

/**
 * 在线用户管理能力接口。
 * <p>
 * 封装会话管理能力，提供踢人、强制登出、在线状态查询等操作。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public interface OnlineUserManager {

    /**
     * 踢出指定用户（使其 Token 失效）。
     *
     * @param userId 用户 ID
     */
    void kickout(Object userId);

    /**
     * 踢出指定用户的指定设备。
     *
     * @param userId 用户 ID
     * @param device 设备标识
     */
    void kickout(Object userId, String device);

    /**
     * 强制指定用户登出。
     *
     * @param userId 用户 ID
     */
    void forceLogout(Object userId);

    /**
     * 强制指定用户的指定设备登出。
     *
     * @param userId 用户 ID
     * @param device 设备标识
     */
    void forceLogout(Object userId, String device);

    /**
     * 判断指定用户是否在线。
     *
     * @param userId 用户 ID
     * @return 是否在线
     */
    boolean isOnline(Object userId);

    /**
     * 获取指定用户的 Token 值。
     *
     * @param userId 用户 ID
     * @return Token 值
     */
    String getTokenValue(Object userId);

    /**
     * 获取当前在线用户数量。
     *
     * @return 在线用户数，获取失败返回 {@code -1}
     */
    long getOnlineCount();

    /**
     * 搜索 Token 列表（分页）。
     *
     * @param keyword 关键词
     * @param page    页码（从 1 开始）
     * @param size    每页大小
     * @return Token ID 列表
     */
    List<String> searchTokens(String keyword, int page, int size);
}
