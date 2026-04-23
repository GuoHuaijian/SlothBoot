package com.sloth.boot.starter.redis.support;

import cn.hutool.core.util.StrUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.reflect.Method;

/**
 * SpEL 表达式解析支持类。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class SpelExpressionSupport {

    private static final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();
    private static final DefaultParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    private SpelExpressionSupport() {
    }

    /**
     * 解析方法上的 SpEL 表达式。
     *
     * @param joinPoint   切点
     * @param expression  表达式
     * @param defaultText 默认值
     * @return 解析结果
     */
    public static String parse(ProceedingJoinPoint joinPoint, String expression, String defaultText) {
        if (StrUtil.isBlank(expression)) {
            return defaultText;
        }
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(
                joinPoint.getTarget(),
                method,
                joinPoint.getArgs(),
                PARAMETER_NAME_DISCOVERER
        );
        if (expression.contains("#")) {
            return EXPRESSION_PARSER.parseExpression(expression).getValue(context, String.class);
        }
        if (expression.contains("{") || expression.contains("}")) {
            return EXPRESSION_PARSER.parseExpression(expression, ParserContext.TEMPLATE_EXPRESSION)
                    .getValue(context, String.class);
        }
        return expression;
    }
}
