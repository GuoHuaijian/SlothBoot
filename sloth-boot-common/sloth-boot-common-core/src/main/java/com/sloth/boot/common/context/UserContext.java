package com.sloth.boot.common.context;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 用户上下文
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@EqualsAndHashCode
public class UserContext implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 使用 TransmittableThreadLocal 存储用户信息
     */
    private static final ThreadLocal<UserInfo> USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 用户信息
     */
    @Data
    public static class UserInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 用户ID
         */
        private Long userId;

        /**
         * 用户名
         */
        private String username;

        /**
         * 租户ID
         */
        private String tenantId;

        /**
         * 角色集合
         */
        private Set<String> roles = new HashSet<>();

        /**
         * 数据范围
         */
        private String dataScope;

        /**
         * 扩展信息
         */
        private Map<String, Object> extra;

        /**
         * 获取用户ID
         *
         * @return 用户ID
         */
        public Long getUserId() {
            return userId;
        }

        /**
         * 获取用户名
         *
         * @return 用户名
         */
        public String getUsername() {
            return username;
        }

        /**
         * 获取租户ID
         *
         * @return 租户ID
         */
        public String getTenantId() {
            return tenantId;
        }

        /**
         * 获取角色集合
         *
         * @return 角色集合
         */
        public Set<String> getRoles() {
            return roles;
        }

        /**
         * 获取数据范围
         *
         * @return 数据范围
         */
        public String getDataScope() {
            return dataScope;
        }

        /**
         * 获取扩展信息
         *
         * @return 扩展信息
         */
        public Map<String, Object> getExtra() {
            return extra;
        }
    }

    /**
     * 设置用户信息
     *
     * @param userInfo 用户信息
     */
    public static void set(UserInfo userInfo) {
        USER_THREAD_LOCAL.set(userInfo);
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    public static UserInfo get() {
        return USER_THREAD_LOCAL.get();
    }

    /**
     * 获取用户ID
     *
     * @return 用户ID
     */
    public static Long getUserId() {
        UserInfo userInfo = get();
        return userInfo != null ? userInfo.getUserId() : null;
    }

    /**
     * 获取用户名
     *
     * @return 用户名
     */
    public static String getUsername() {
        UserInfo userInfo = get();
        return userInfo != null ? userInfo.getUsername() : null;
    }

    /**
     * 获取租户ID
     *
     * @return 租户ID
     */
    public static String getTenantId() {
        UserInfo userInfo = get();
        return userInfo != null ? userInfo.getTenantId() : null;
    }

    /**
     * 获取角色集合
     *
     * @return 角色集合
     */
    public static Set<String> getRoles() {
        UserInfo userInfo = get();
        return userInfo != null ? userInfo.getRoles() : CollUtil.newHashSet();
    }

    /**
     * 获取数据范围
     *
     * @return 数据范围
     */
    public static String getDataScope() {
        UserInfo userInfo = get();
        return userInfo != null ? userInfo.getDataScope() : null;
    }

    /**
     * 获取扩展信息
     *
     * @return 扩展信息
     */
    public static Map<String, Object> getExtra() {
        UserInfo userInfo = get();
        return userInfo != null ? userInfo.getExtra() : null;
    }

    /**
     * 清除用户信息
     */
    public static void clear() {
        USER_THREAD_LOCAL.remove();
    }
}
