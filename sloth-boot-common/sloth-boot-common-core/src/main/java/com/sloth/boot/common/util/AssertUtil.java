package com.sloth.boot.common.util;

import com.sloth.boot.common.exception.BizException;
import com.sloth.boot.common.exception.ErrorCode;

import java.util.Collection;

/**
 * 断言工具类
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class AssertUtil {

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
     * @param obj  对象
     * @param msg  错误信息
     */
    public static void notNull(Object obj, String msg) {
        if (obj == null) {
            throw new BizException(msg);
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
            throw new BizException(msg);
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
            throw new BizException(msg);
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
            throw new BizException(msg);
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
            throw new BizException(msg);
        }
    }
}
