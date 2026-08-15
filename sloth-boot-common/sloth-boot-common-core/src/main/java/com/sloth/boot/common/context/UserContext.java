package com.sloth.boot.common.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
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
     * 使用 TransmittableThreadLocal 存储用户信息，支持异步线程上下文传递
     */
    private static final TransmittableThreadLocal<UserInfo> USER_THREAD_LOCAL = new TransmittableThreadLocal<>();

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
        @Getter(AccessLevel.NONE)
        @Setter(AccessLevel.NONE)
        private Set<String> roles = new LinkedHashSet<>();

        /**
         * 获取用户角色集合（不可修改视图）。
         *
         * @return 角色集合
         */
        public Set<String> getRoles() {
            return Collections.unmodifiableSet(roles);
        }

        /**
         * 设置用户角色集合。
         *
         * @param roles 角色集合
         */
        public void setRoles(Set<String> roles) {
            this.roles = roles != null ? new LinkedHashSet<>(roles) : new LinkedHashSet<>();
        }

        /**
         * 数据范围
         */
        private String dataScope;

        /**
         * 扩展信息
         */
        @Getter(AccessLevel.NONE)
        @Setter(AccessLevel.NONE)
        private Map<String, Object> extra;

        /**
         * 获取扩展信息（不可修改视图）。
         *
         * @return 扩展信息
         */
        public Map<String, Object> getExtra() {
            return extra != null ? Collections.unmodifiableMap(extra) : null;
        }

        /**
         * 设置扩展信息。
         *
         * @param extra 扩展信息
         */
        public void setExtra(Map<String, Object> extra) {
            this.extra = extra != null ? new HashMap<>(extra) : null;
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
        return userInfo != null ? userInfo.getRoles() : Collections.emptySet();
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
