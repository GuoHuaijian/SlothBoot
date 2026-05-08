package com.sloth.boot.common.util;

import com.sloth.boot.common.exception.BizException;
import com.sloth.boot.common.exception.ErrorCode;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

/**
 * 断言工具类
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class AssertUtil {

    private AssertUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 断言对象不为空，否则抛出业务异常
     *
     * @param obj       对象
     * @param errorCode 错误码
     */
    public static void notNull(Object obj, ErrorCode errorCode) {
        if (obj == null) {
            throw new BizException(errorCode);
        }
    }

    /**
     * 断言对象不为空，否则抛出业务异常
     *
     * @param obj 对象
     * @param msg 错误信息
     */
    public static void notNull(Object obj, String msg) {
        if (obj == null) {
            throw BizException.of(msg);
        }
    }

    /**
     * 断言字符串不为空，否则抛出业务异常
     *
     * @param str 字符串
     * @param msg 错误信息
     */
    public static void notEmpty(String str, String msg) {
        if (str == null || str.trim().isEmpty()) {
            throw BizException.of(msg);
        }
    }

    /**
     * 断言字符串不为空白，否则抛出业务异常
     * <p>
     * 比 {@link #notEmpty(String, String)} 更严格，不仅检查 null 和空字符串，
     * 还拒绝纯空白字符串。
     *
     * @param str 字符串
     * @param msg 错误信息
     */
    public static void notBlank(String str, String msg) {
        if (str == null || str.isBlank()) {
            throw BizException.of(msg);
        }
    }

    /**
     * 断言字符串不为空白，否则抛出业务异常
     *
     * @param str       字符串
     * @param errorCode 错误码
     */
    public static void notBlank(String str, ErrorCode errorCode) {
        if (str == null || str.isBlank()) {
            throw new BizException(errorCode);
        }
    }

    /**
     * 断言集合不为空，否则抛出业务异常
     *
     * @param coll 集合
     * @param msg  错误信息
     */
    public static void notEmpty(Collection<?> coll, String msg) {
        if (coll == null || coll.isEmpty()) {
            throw BizException.of(msg);
        }
    }

    /**
     * 断言 Map 不为空，否则抛出业务异常
     *
     * @param map Map
     * @param msg 错误信息
     */
    public static void notEmpty(Map<?, ?> map, String msg) {
        if (map == null || map.isEmpty()) {
            throw BizException.of(msg);
        }
    }

    /**
     * 断言 Map 不为空，否则抛出业务异常
     *
     * @param map       Map
     * @param errorCode 错误码
     */
    public static void notEmpty(Map<?, ?> map, ErrorCode errorCode) {
        if (map == null || map.isEmpty()) {
            throw new BizException(errorCode);
        }
    }

    /**
     * 断言数组不为空，否则抛出业务异常
     *
     * @param array 数组
     * @param msg   错误信息
     */
    public static void notEmpty(Object[] array, String msg) {
        if (array == null || array.length == 0) {
            throw BizException.of(msg);
        }
    }

    /**
     * 断言数组不为空，否则抛出业务异常
     *
     * @param array     数组
     * @param errorCode 错误码
     */
    public static void notEmpty(Object[] array, ErrorCode errorCode) {
        if (array == null || array.length == 0) {
            throw new BizException(errorCode);
        }
    }

    /**
     * 断言条件为真，否则抛出业务异常
     *
     * @param condition 条件
     * @param msg       错误信息
     */
    public static void isTrue(boolean condition, String msg) {
        if (!condition) {
            throw BizException.of(msg);
        }
    }

    /**
     * 断言条件为假，否则抛出业务异常
     *
     * @param condition 条件
     * @param msg       错误信息
     */
    public static void isFalse(boolean condition, String msg) {
        if (condition) {
            throw BizException.of(msg);
        }
    }

    /**
     * 断言条件为假，否则抛出业务异常
     *
     * @param condition 条件
     * @param errorCode 错误码
     */
    public static void isFalse(boolean condition, ErrorCode errorCode) {
        if (condition) {
            throw new BizException(errorCode);
        }
    }

    /**
     * 断言两个对象相等，否则抛出业务异常
     *
     * @param a   对象a
     * @param b   对象b
     * @param msg 错误信息
     */
    public static void equals(Object a, Object b, String msg) {
        if (a == null ? b != null : !a.equals(b)) {
            throw BizException.of(msg);
        }
    }

    /**
     * 断言整数值在指定范围内，否则抛出业务异常
     *
     * @param value 待检查的值
     * @param min   最小值（包含）
     * @param max   最大值（包含）
     * @param msg   错误信息
     */
    public static void inRange(int value, int min, int max, String msg) {
        if (value < min || value > max) {
            throw BizException.of(msg);
        }
    }

    /**
     * 断言长整数值在指定范围内，否则抛出业务异常
     *
     * @param value 待检查的值
     * @param min   最小值（包含）
     * @param max   最大值（包含）
     * @param msg   错误信息
     */
    public static void inRange(long value, long min, long max, String msg) {
        if (value < min || value > max) {
            throw BizException.of(msg);
        }
    }

    /**
     * 断言 BigDecimal 值在指定范围内，否则抛出业务异常
     *
     * @param value 待检查的值
     * @param min   最小值（包含）
     * @param max   最大值（包含）
     * @param msg   错误信息
     */
    public static void inRange(BigDecimal value, BigDecimal min, BigDecimal max, String msg) {
        if (value == null || value.compareTo(min) < 0 || value.compareTo(max) > 0) {
            throw BizException.of(msg);
        }
    }
}
