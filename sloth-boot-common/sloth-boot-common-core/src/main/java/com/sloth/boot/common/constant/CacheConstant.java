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

    /**
     * 缓存键分隔符
     */
    public static final String SEPARATOR = ":";

    /**
     * 验证码缓存前缀
     */
    public static final String CODE_CACHE_PREFIX = "sloth:code";

    /**
     * 限流缓存前缀
     */
    public static final String RATE_LIMIT_PREFIX = "sloth:rateLimit";

    /**
     * 幂等缓存前缀
     */
    public static final String IDEMPOTENT_PREFIX = "sloth:idempotent";

    /**
     * 分布式锁前缀
     */
    public static final String LOCK_PREFIX = "sloth:lock";

    /**
     * 操作日志缓存前缀
     */
    public static final String OPERATE_LOG_PREFIX = "sloth:operateLog";

    /**
     * 验证码缓存过期时间：5分钟
     */
    public static final long CODE_EXPIRE_TIME = 5 * 60 * 1000L;

    /**
     * 空值缓存时间：2分钟
     */
    public static final long NULL_VALUE_EXPIRE_TIME = 2 * 60 * 1000L;

    /**
     * 缓存键通配符：*
     */
    public static final String CACHE_WILDCARD = "*";

    /**
     * 缓存键通配符：？
     */
    public static final String CACHE_SINGLE_WILDCARD = "?";
}
