package com.sloth.boot.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 幂等注解
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {

    /**
     * 幂等有效时间，单位秒
     */
    int timeout() default 10;

    /**
     * 提示消息
     */
    String message() default "请勿重复操作";

    /**
     * 幂等 key
     */
    String key() default "";
}
