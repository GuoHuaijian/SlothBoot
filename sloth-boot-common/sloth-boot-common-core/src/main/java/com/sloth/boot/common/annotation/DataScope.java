package com.sloth.boot.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据范围注解
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataScope {

    /**
     * 部门别名
     */
    String deptAlias() default "";

    /**
     * 用户别名
     */
    String userAlias() default "";
}
