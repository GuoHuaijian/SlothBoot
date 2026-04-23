package com.sloth.boot.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 脱敏注解
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Desensitize {

    /**
     * 脱敏类型
     */
    DesensitizeType type();

    /**
     * 前缀保留长度
     */
    int prefixLen() default 0;

    /**
     * 后缀保留长度
     */
    int suffixLen() default 0;
}
