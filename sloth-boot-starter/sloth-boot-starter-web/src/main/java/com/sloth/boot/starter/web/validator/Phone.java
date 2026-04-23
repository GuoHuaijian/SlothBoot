package com.sloth.boot.starter.web.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 手机号校验注解。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneValidator.class)
public @interface Phone {

    /**
     * 默认错误信息。
     *
     * @return 错误信息
     */
    String message() default "手机号格式不正确";

    /**
     * 分组。
     *
     * @return 分组
     */
    Class<?>[] groups() default {};

    /**
     * 负载。
     *
     * @return 负载
     */
    Class<? extends Payload>[] payload() default {};
}
