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
 * 通用常量
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class CommonConstant {

    private CommonConstant() {
    }

    // ==================== 编码相关 ====================
    /**
     * UTF-8 编码
     */
    public static final String UTF8 = "UTF-8";

    /**
     * GBK 编码
     */
    public static final String GBK = "GBK";

    // ==================== 分隔符 ====================
    /**
     * 斜杠
     */
    public static final String SLASH = "/";

    /**
     * 逗号
     */
    public static final String COMMA = ",";

    /**
     * 点
     */
    public static final String DOT = ".";

    /**
     * 冒号
     */
    public static final String COLON = ":";

    /**
     * 下划线
     */
    public static final String UNDERSCORE = "_";

    /**
     * 连字符
     */
    public static final String HYPHEN = "-";

    // ==================== 空值 ====================
    /**
     * 空字符串
     */
    public static final String EMPTY = "";

    // ==================== 状态码 ====================
    /**
     * 成功
     */
    public static final int SUCCESS = 0;

    /**
     * 失败
     */
    public static final int FAIL = -1;

    // ==================== 是/否标记 ====================
    /**
     * 是
     */
    public static final String Y = "Y";

    /**
     * 否
     */
    public static final String N = "N";

    // ==================== 数字常量 ====================
    /**
     * 零
     */
    public static final int NUM_ZERO = 0;

    /**
     * 一
     */
    public static final int NUM_ONE = 1;

    /**
     * 十
     */
    public static final int NUM_TEN = 10;

    /**
     * 一百
     */
    public static final int NUM_HUNDRED = 100;

    /**
     * 一千
     */
    public static final int NUM_THOUSAND = 1000;

    /**
     * 一万
     */
    public static final int NUM_TEN_THOUSAND = 10000;

    /**
     * 一百万
     */
    public static final int NUM_MILLION = 1000000;

    /**
     * 一千万
     */
    public static final int NUM_TEN_MILLION = 10000000;

    /**
     * 一亿
     */
    public static final int NUM_HUNDRED_MILLION = 100000000;
}
