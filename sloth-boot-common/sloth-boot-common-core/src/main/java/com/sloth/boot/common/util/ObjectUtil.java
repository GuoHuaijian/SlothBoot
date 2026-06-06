package com.sloth.boot.common.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * 对象工具类。
 * <p>
 * 提供综合判空（支持 String/Collection/Map/Array）和空值安全转换方法。
 * <p>
 * 对于简单的 null 判断（{@code == null}）和相等比较，请直接使用 Java 语法或 {@link java.util.Objects}，
 * 不需要包装为工具方法。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class ObjectUtil {

    private ObjectUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 如果对象为 null 则返回默认值。
     *
     * @param obj          对象
     * @param defaultValue 默认值
     * @param <T>          对象类型
     * @return 对象本身或默认值
     */
    public static <T> T defaultIfNull(T obj, T defaultValue) {
        return obj != null ? obj : defaultValue;
    }

    /**
     * 返回第一个非 null 的值。
     *
     * @param values 值列表
     * @param <T>    值类型
     * @return 第一个非 null 的值，全部为 null 则返回 null
     */
    @SafeVarargs
    public static <T> T firstNonNull(T... values) {
        if (values != null) {
            for (T value : values) {
                if (value != null) {
                    return value;
                }
            }
        }
        return null;
    }

    /**
     * 综合判空。
     * <p>
     * 判断对象是否为以下空状态之一：
     * <ul>
     *   <li>null</li>
     *   <li>空字符串 ""</li>
     *   <li>空集合</li>
     *   <li>空 Map</li>
     *   <li>空数组</li>
     * </ul>
     *
     * @param obj 对象
     * @return 是否为空
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof String s) {
            return s.isEmpty();
        }
        if (obj instanceof CharSequence cs) {
            return cs.length() == 0;
        }
        if (obj instanceof Collection<?> c) {
            return c.isEmpty();
        }
        if (obj instanceof Map<?, ?> m) {
            return m.isEmpty();
        }
        if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        }
        return false;
    }

    /**
     * 综合非空判断。
     *
     * @param obj 对象
     * @return 是否非空
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }
}
