package com.sloth.boot.common.util;

import com.sloth.boot.common.enums.IBaseEnum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 枚举工具类
 * <p>
 * 提供基于 {@link IBaseEnum} 接口的枚举查询方法。
 * <p>
 * 使用示例：
 * <pre>
 * // 按 code 查询（未找到返回 null，不抛异常）
 * StatusEnum status = EnumUtil.fromCode(StatusEnum.class, 1);
 *
 * // 按 code 查询（带默认值）
 * StatusEnum status = EnumUtil.fromCode(StatusEnum.class, 999, StatusEnum.DISABLE);
 *
 * // 获取 code 对应的描述
 * String desc = EnumUtil.getDesc(StatusEnum.class, 1); // "启用"
 *
 * // 获取 code→desc 映射
 * Map&lt;Integer, String&gt; map = EnumUtil.toMap(StatusEnum.class);
 * // {1=启用, 0=禁用}
 * </pre>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class EnumUtil {

    private EnumUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 根据 code 查找枚举（未找到返回 null）
     *
     * @param clazz 枚举类
     * @param code  枚举 code
     * @param <E>   枚举类型
     * @return 枚举实例，未找到返回 null
     */
    public static <E extends Enum<E> & IBaseEnum> E fromCode(Class<E> clazz, int code) {
        if (clazz == null) {
            return null;
        }
        for (E e : clazz.getEnumConstants()) {
            if (e.getCode() == code) {
                return e;
            }
        }
        return null;
    }

    /**
     * 根据 code 查找枚举（未找到返回默认值）
     *
     * @param clazz        枚举类
     * @param code         枚举 code
     * @param defaultValue 默认值
     * @param <E>          枚举类型
     * @return 枚举实例，未找到返回默认值
     */
    public static <E extends Enum<E> & IBaseEnum> E fromCode(Class<E> clazz, int code, E defaultValue) {
        E result = fromCode(clazz, code);
        return result != null ? result : defaultValue;
    }

    /**
     * 根据描述查找枚举（未找到返回 null）
     *
     * @param clazz 枚举类
     * @param desc  枚举描述
     * @param <E>   枚举类型
     * @return 枚举实例，未找到返回 null
     */
    public static <E extends Enum<E> & IBaseEnum> E fromDesc(Class<E> clazz, String desc) {
        if (clazz == null || desc == null) {
            return null;
        }
        for (E e : clazz.getEnumConstants()) {
            if (desc.equals(e.getDesc())) {
                return e;
            }
        }
        return null;
    }

    /**
     * 获取 code 对应的描述
     *
     * @param clazz 枚举类
     * @param code  枚举 code
     * @param <E>   枚举类型
     * @return 描述，未找到返回 null
     */
    public static <E extends Enum<E> & IBaseEnum> String getDesc(Class<E> clazz, int code) {
        E e = fromCode(clazz, code);
        return e == null ? null : e.getDesc();
    }

    /**
     * 获取所有枚举 code 列表
     *
     * @param clazz 枚举类
     * @param <E>   枚举类型
     * @return code 列表
     */
    public static <E extends Enum<E> & IBaseEnum> List<Integer> allCodes(Class<E> clazz) {
        if (clazz == null) {
            return Collections.emptyList();
        }
        List<Integer> codes = new ArrayList<>();
        for (E e : clazz.getEnumConstants()) {
            codes.add(e.getCode());
        }
        return codes;
    }

    /**
     * 获取所有枚举描述列表
     *
     * @param clazz 枚举类
     * @param <E>   枚举类型
     * @return 描述列表
     */
    public static <E extends Enum<E> & IBaseEnum> List<String> allDescs(Class<E> clazz) {
        if (clazz == null) {
            return Collections.emptyList();
        }
        List<String> descs = new ArrayList<>();
        for (E e : clazz.getEnumConstants()) {
            descs.add(e.getDesc());
        }
        return descs;
    }

    /**
     * 获取 code→desc 映射
     *
     * @param clazz 枚举类
     * @param <E>   枚举类型
     * @return code→desc 映射
     */
    public static <E extends Enum<E> & IBaseEnum> Map<Integer, String> toMap(Class<E> clazz) {
        if (clazz == null) {
            return Collections.emptyMap();
        }
        Map<Integer, String> map = new LinkedHashMap<>();
        for (E e : clazz.getEnumConstants()) {
            map.put(e.getCode(), e.getDesc());
        }
        return map;
    }
}
