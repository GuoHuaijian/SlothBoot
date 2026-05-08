package com.sloth.boot.common.test.mock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 测试用户注解。
 * <p>
 * 标注在测试方法或测试类上，自动填充 {@code UserContext}，避免每个测试手动设置用户上下文。
 * <p>
 * 使用示例：
 * <pre>
 * &#64;Test
 * &#64;MockUser(userId = 1L, username = "admin", tenantId = "t1")
 * void testWithUser() {
 *     assertEquals(1L, UserContext.getUserId());
 * }
 * </pre>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MockUser {

    /**
     * 模拟用户 ID。
     */
    long userId() default 1L;

    /**
     * 模拟用户名。
     */
    String username() default "test-user";

    /**
     * 模拟租户 ID。
     */
    String tenantId() default "";

    /**
     * 模拟角色列表。
     */
    String[] roles() default {};

    /**
     * 模拟数据权限范围。
     */
    String dataScope() default "";
}
