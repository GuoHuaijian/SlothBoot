package com.sloth.boot.starter.mybatis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据权限注解。
 * <p>
 * 标注在 Mapper 方法上，通过 SpEL 表达式动态追加数据权限 WHERE 条件。
 * 比 {@link com.sloth.boot.common.annotation.DataScope} 更灵活，支持自定义表达式。
 * <p>
 * 使用示例：
 * <pre>
 * &#64;DataPermission(expression = "#deptAlias.dept_id = {deptId}")
 * List&lt;User&gt; selectUserList();
 * </pre>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataPermission {

    /**
     * 数据权限 WHERE 条件的 SpEL 表达式。
     * <p>
     * 可用变量：
     * <ul>
     *   <li>{@code {userId}} — 当前用户 ID</li>
     *   <li>{@code {deptId}} — 当前用户部门 ID（从 extra 中获取）</li>
     *   <li>{@code {username}} — 当前用户名</li>
     *   <li>{@code {tenantId}} — 当前租户 ID</li>
     * </ul>
     *
     * @return SpEL 表达式
     */
    String expression() default "";

    /**
     * 数据权限范围类型（与 {@link com.sloth.boot.common.annotation.DataScope} 兼容）。
     * <p>
     * 当 expression 为空时，使用此字段走传统范围逻辑。
     *
     * @return 范围类型
     */
    String scopeType() default "all";

    /**
     * 部门表别名。
     */
    String deptAlias() default "";

    /**
     * 用户表别名。
     */
    String userAlias() default "";
}
