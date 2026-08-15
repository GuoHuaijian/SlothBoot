package com.sloth.boot.common.util;

/**
 * ID 工具类
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class IdUtil {

    private IdUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 生成快速简单 UUID
     *
     * @return UUID 字符串
     */
    public static String fastSimpleUUID() {
        return cn.hutool.core.util.IdUtil.fastSimpleUUID();
    }

    /**
     * 生成 Nano ID
     *
     * @return Nano ID 字符串
     */
    public static String nanoId() {
        return cn.hutool.core.util.IdUtil.nanoId();
    }

    /**
     * 生成指定长度的 Nano ID
     *
     * @param size 长度
     * @return Nano ID 字符串
     */
    public static String nanoId(int size) {
        return cn.hutool.core.util.IdUtil.nanoId(size);
    }
}
