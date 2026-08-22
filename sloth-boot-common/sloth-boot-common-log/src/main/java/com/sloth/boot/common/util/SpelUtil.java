package com.sloth.boot.common.util;

import cn.hutool.core.util.StrUtil;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.reflect.Method;

/**
 * SpEL 表达式解析工具类。
 * <p>
 * 提供基于方法参数的 SpEL 表达式解析能力，支持三种模式：
 * <ul>
 *   <li>空表达式：返回默认值</li>
 *   <li>普通 SpEL：包含 {@code #} 前缀的表达式，如 {@code #user.name}</li>
 *   <li>模板表达式：包含 {@code {}} 的表达式，如 {@code #{#orderId + '_' + #userId}}</li>
 * </ul>
 * <p>
 * 使用示例：
 * <pre>
 * // 方法签名：void process(String userId, String orderId)
 * // 在 AOP 中使用（从 ProceedingJoinPoint 获取 target、method 和 args）
 * MethodSignature signature = (MethodSignature) joinPoint.getSignature();
 * Method method = signature.getMethod();
 * String key = SpelUtil.parse(joinPoint.getTarget(), method, joinPoint.getArgs(), "#userId", "default");
 *
 * // 解析模板表达式
 * String key = SpelUtil.parse(target, method, args, "#{#userId + '_' + #orderId}", "default");
 * </pre>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class SpelUtil {

    private static final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();
    private static final DefaultParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    /**
     * 模板表达式前缀
     */
    private static final String TEMPLATE_PREFIX = "#{";

    /**
     * 变量引用前缀
     */
    private static final String VARIABLE_PREFIX = "#";

    private SpelUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 解析 SpEL 表达式。
     *
     * @param target       目标对象（方法所属实例）
     * @param method       目标方法
     * @param args         方法参数值
     * @param expression   SpEL 表达式
     * @param defaultValue 默认值（表达式为空时返回）
     * @return 解析结果
     */
    public static String parse(Object target, Method method, Object[] args, String expression, String defaultValue) {
        if (StrUtil.isBlank(expression)) {
            return defaultValue;
        }
        MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(
            target,
            method,
            args,
            PARAMETER_NAME_DISCOVERER
        );
        // 模板表达式以 #{ 开头，必须先于普通 SpEL 判定，否则会被当作变量解析失败
        if (expression.contains(TEMPLATE_PREFIX)) {
            return EXPRESSION_PARSER.parseExpression(expression, ParserContext.TEMPLATE_EXPRESSION)
                .getValue(context, String.class);
        }
        if (expression.contains(VARIABLE_PREFIX)) {
            return EXPRESSION_PARSER.parseExpression(expression).getValue(context, String.class);
        }
        return expression;
    }
}
