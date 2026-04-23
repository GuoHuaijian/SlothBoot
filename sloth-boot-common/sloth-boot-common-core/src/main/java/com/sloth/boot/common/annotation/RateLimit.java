package com.sloth.boot.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限流注解
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /**
     * 限流次数
     */
    int count() default 100;

    /**
     * 时间窗口，单位秒
     */
    int period() default 60;

    /**
     * 限流 key
     */
    String key() default "";

    /**
     * 提示消息
     */
    String message() default "访问过于频繁";

    /**
     * 限流类型
     */
    LimitType type() default LimitType.DEFAULT;
}
