package com.sloth.boot.common.util;

import cn.hutool.core.lang.Snowflake;

/**
 * ID 工具类
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class IdUtil {

    /**
     * 雪花算法实例
     */
    private static final Snowflake SNOWFLAKE = IdUtil.getSnowflake(1, 1);

    /**
     * 生成雪花 ID
     *
     * @return 雪花 ID
     */
    public static long snowflakeId() {
        return SNOWFLAKE.nextId();
    }

    /**
     * 生成雪花 ID 字符串
     *
     * @return 雪花 ID 字符串
     */
    public static String snowflakeIdStr() {
        return String.valueOf(snowflakeId());
    }

    /**
     * 生成快速简单 UUID
     *
     * @return UUID 字符串
     */
    public static String fastSimpleUUID() {
        return IdUtil.fastSimpleUUID();
    }

    /**
     * 生成 Nano ID
     *
     * @return Nano ID 字符串
     */
    public static String nanoId() {
        return IdUtil.nanoId();
    }

    /**
     * 生成指定长度的 Nano ID
     *
     * @param size 长度
     * @return Nano ID 字符串
     */
    public static String nanoId(int size) {
        return IdUtil.nanoId(size);
    }
}
