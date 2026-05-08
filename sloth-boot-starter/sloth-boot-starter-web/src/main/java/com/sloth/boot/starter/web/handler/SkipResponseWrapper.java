package com.sloth.boot.starter.web.handler;

import java.lang.annotation.*;

/**
 * 跳过统一响应包装注解。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SkipResponseWrapper {
}
