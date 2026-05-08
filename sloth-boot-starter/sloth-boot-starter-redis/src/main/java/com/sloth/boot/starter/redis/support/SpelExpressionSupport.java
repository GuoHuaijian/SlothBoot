package com.sloth.boot.starter.redis.support;

import com.sloth.boot.common.util.SpelUtil;
import cn.hutool.core.util.StrUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * SpEL 表达式解析支持类。
 *
 * @author sloth-boot
 * @since 1.0.0
 * @deprecated 请使用 {@link SpelUtil} 代替，此类将在未来版本中移除
 */
@Deprecated(forRemoval = true)
public final class SpelExpressionSupport {

    private SpelExpressionSupport() {
    }

    /**
     * 解析方法上的 SpEL 表达式。
     *
     * @param joinPoint   切点
     * @param expression  表达式
     * @param defaultText 默认值
     * @return 解析结果
     * @deprecated 请使用 {@link SpelUtil#parse(Object, Method, Object[], String, String)}
     */
    @Deprecated(forRemoval = true)
    public static String parse(ProceedingJoinPoint joinPoint, String expression, String defaultText) {
        if (StrUtil.isBlank(expression)) {
            return defaultText;
        }
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        return SpelUtil.parse(joinPoint.getTarget(), method, joinPoint.getArgs(), expression, defaultText);
    }
}
