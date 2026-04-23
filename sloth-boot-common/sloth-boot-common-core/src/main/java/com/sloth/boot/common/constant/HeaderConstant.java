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
 * HTTP 请求头常量
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class HeaderConstant {

    private HeaderConstant() {
    }

    // ==================== 链路追踪 ====================
    /**
     * 链路追踪ID
     */
    public static final String TRACE_ID = "X-Trace-Id";

    // ==================== 用户信息 ====================
    /**
     * 用户ID
     */
    public static final String USER_ID = "X-User-Id";

    /**
     * 用户名
     */
    public static final String USERNAME = "X-Username";

    /**
     * 租户ID
     */
    public static final String TENANT_ID = "X-Tenant-Id";

    /**
     * 认证令牌
     */
    public static final String TOKEN = "Authorization";

    // ==================== 内部调用 ====================
    /**
     * 内部调用标识
     */
    public static final String INNER_CALL = "X-Inner-Call";

    /**
     * 内部调用令牌
     */
    public static final String INNER_CALL_TOKEN = "sloth-boot-inner-call-secret";

    // ==================== 客户端信息 ====================
    /**
     * 客户端IP
     */
    public static final String CLIENT_IP = "X-Real-IP";

    /**
     * 请求来源
     */
    public static final String REQUEST_FROM = "X-Request-From";
}
