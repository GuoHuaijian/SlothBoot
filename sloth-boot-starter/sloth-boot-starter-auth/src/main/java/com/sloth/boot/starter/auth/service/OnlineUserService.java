package com.sloth.boot.starter.auth.service;

import java.util.List;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 在线用户管理服务。
 * <p>
 * 封装 Sa-Token 的会话管理能力，提供踢人、强制登出等操作。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
public class OnlineUserService implements OnlineUserManager {

    /**
     * 踢出指定用户（使其 Token 失效）。
     *
     * @param userId 用户 ID
     */
    public void kickout(Object userId) {
        log.info("[Auth] 踢出用户: {}", userId);
        StpUtil.kickout(userId);
    }

    /**
     * 踢出指定用户的指定设备。
     *
     * @param userId 用户 ID
     * @param device 设备标识
     */
    public void kickout(Object userId, String device) {
        log.info("[Auth] 踢出用户设备: userId={}, device={}", userId, device);
        StpUtil.kickout(userId, device);
    }

    /**
     * 强制指定用户登出。
     *
     * @param userId 用户 ID
     */
    public void forceLogout(Object userId) {
        log.info("[Auth] 强制登出用户: {}", userId);
        StpUtil.logout(userId);
    }

    /**
     * 强制指定用户的指定设备登出。
     *
     * @param userId 用户 ID
     * @param device 设备标识
     */
    public void forceLogout(Object userId, String device) {
        log.info("[Auth] 强制登出用户设备: userId={}, device={}", userId, device);
        StpUtil.logout(userId, device);
    }

    /**
     * 判断指定用户是否在线。
     *
     * @param userId 用户 ID
     * @return 是否在线
     */
    public boolean isOnline(Object userId) {
        return StpUtil.isLogin(userId);
    }

    /**
     * 获取指定用户的 Token 值。
     *
     * @param userId 用户 ID
     * @return Token 值
     */
    public String getTokenValue(Object userId) {
        return StpUtil.getTokenValueByLoginId(userId);
    }

    /**
     * 获取当前在线用户数量（通过 Sa-Token Session 估算）。
     * <p>
     * 失败时返回 {@code -1}，调用方应检查此哨兵值。
     *
     * @return 在线用户数，获取失败返回 {@code -1}
     */
    public long getOnlineCount() {
        try {
            return StpUtil.searchTokenSessionId("", 0, -1, false).size();
        } catch (Exception e) {
            log.warn("[Auth] 获取在线用户数失败", e);
            return -1;
        }
    }

    /**
     * 搜索 Token 列表（分页）。
     *
     * @param keyword 关键词
     * @param page    页码（从 1 开始）
     * @param size    每页大小
     * @return Token ID 列表
     */
    public List<String> searchTokens(String keyword, int page, int size) {
        return StpUtil.searchTokenSessionId(keyword, page - 1, size, false);
    }
}
