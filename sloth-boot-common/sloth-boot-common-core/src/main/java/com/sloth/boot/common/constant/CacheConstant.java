/*
 * Copyright 2025 Sloth Boot
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sloth.boot.common.constant;

/**
 * 缓存常量
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class CacheConstant {

    private CacheConstant() {
    }

    // ==================== 缓存基础配置 ====================
    /**
     * 默认缓存 key 前缀
     */
    public static final String DEFAULT_CACHE_PREFIX = "sloth:";

    /**
     * 缓存键分隔符
     */
    public static final String SEPARATOR = ":";

    // ==================== 业务域缓存前缀 ====================
    /**
     * 用户缓存前缀
     */
    public static final String USER_CACHE_PREFIX = DEFAULT_CACHE_PREFIX + "user";

    /**
     * 角色缓存前缀
     */
    public static final String ROLE_CACHE_PREFIX = DEFAULT_CACHE_PREFIX + "role";

    /**
     * 菜单缓存前缀
     */
    public static final String MENU_CACHE_PREFIX = DEFAULT_CACHE_PREFIX + "menu";

    /**
     * 部门缓存前缀
     */
    public static final String DEPT_CACHE_PREFIX = DEFAULT_CACHE_PREFIX + "dept";

    /**
     * 字典缓存前缀
     */
    public static final String DICT_CACHE_PREFIX = DEFAULT_CACHE_PREFIX + "dict";

    /**
     * 配置缓存前缀
     */
    public static final String CONFIG_CACHE_PREFIX = DEFAULT_CACHE_PREFIX + "config";

    /**
     * 验证码缓存前缀
     */
    public static final String CODE_CACHE_PREFIX = DEFAULT_CACHE_PREFIX + "code";

    /**
     * 限流缓存前缀
     */
    public static final String RATE_LIMIT_PREFIX = DEFAULT_CACHE_PREFIX + "rateLimit";

    /**
     * 幂等缓存前缀
     */
    public static final String IDEMPOTENT_PREFIX = DEFAULT_CACHE_PREFIX + "idempotent";

    /**
     * 分布式锁前缀
     */
    public static final String LOCK_PREFIX = DEFAULT_CACHE_PREFIX + "lock";

    /**
     * 操作日志缓存前缀
     */
    public static final String OPERATE_LOG_PREFIX = DEFAULT_CACHE_PREFIX + "operateLog";

    // ==================== 缓存时间 ====================
    /**
     * 缓存默认过期时间：30分钟
     */
    public static final long DEFAULT_EXPIRE_TIME = 30 * 60 * 1000L;

    /**
     * 用户缓存过期时间：2小时
     */
    public static final long USER_EXPIRE_TIME = 2 * 60 * 60 * 1000L;

    /**
     * 角色缓存过期时间：2小时
     */
    public static final long ROLE_EXPIRE_TIME = 2 * 60 * 60 * 1000L;

    /**
     * 菜单缓存过期时间：2小时
     */
    public static final long MENU_EXPIRE_TIME = 2 * 60 * 60 * 1000L;

    /**
     * 部门缓存过期时间：2小时
     */
    public static final long DEPT_EXPIRE_TIME = 2 * 60 * 60 * 1000L;

    /**
     * 字典缓存过期时间：24小时
     */
    public static final long DICT_EXPIRE_TIME = 24 * 60 * 60 * 1000L;

    /**
     * 配置缓存过期时间：1小时
     */
    public static final long CONFIG_EXPIRE_TIME = 60 * 60 * 1000L;

    /**
     * 验证码缓存过期时间：5分钟
     */
    public static final long CODE_EXPIRE_TIME = 5 * 60 * 1000L;

    /**
     * 空值缓存时间：2分钟
     */
    public static final long NULL_VALUE_EXPIRE_TIME = 2 * 60 * 1000L;

    // ==================== 缓存通配符 ====================
    /**
     * 缓存键通配符：*
     */
    public static final String CACHE_WILDCARD = "*";

    /**
     * 缓存键通配符：？
     */
    public static final String CACHE_SINGLE_WILDCARD = "?";
}
