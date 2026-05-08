package com.sloth.boot.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常工具类
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class ExceptionUtil {

    private ExceptionUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 检查异常链中是否包含指定类名的异常
     * <p>
     * 同时检查类层级（父类）和 cause 链。
     *
     * @param throwable 异常
     * @param className 异常类全限定名
     * @return 是否包含
     */
    public static boolean hasExceptionName(Throwable throwable, String className) {
        if (throwable == null || className == null) {
            return false;
        }
        Throwable current = throwable;
        while (current != null) {
            Class<?> type = current.getClass();
            while (type != null) {
                if (className.equals(type.getName())) {
                    return true;
                }
                type = type.getSuperclass();
            }
            current = current.getCause();
        }
        return false;
    }

    /**
     * 检查异常链中是否包含指定类型的异常
     *
     * @param throwable 异常
     * @param exceptionType 异常类型
     * @return 是否包含
     */
    public static boolean hasExceptionType(Throwable throwable, Class<? extends Throwable> exceptionType) {
        if (throwable == null || exceptionType == null) {
            return false;
        }
        Throwable current = throwable;
        while (current != null) {
            if (exceptionType.isInstance(current)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    /**
     * 获取根本原因（最深层的 cause）
     *
     * @param throwable 异常
     * @return 根本原因
     */
    public static Throwable getRootCause(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        Throwable root = throwable;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        return root;
    }

    /**
     * 异常堆栈转字符串
     *
     * @param throwable 异常
     * @return 堆栈字符串
     */
    public static String getStackTraceAsString(Throwable throwable) {
        if (throwable == null) {
            return "";
        }
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
