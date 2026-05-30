package com.sloth.boot.common.security.desensitize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 脱敏注解。
 * <p>
 * 标注在字段上，序列化时自动对敏感数据进行脱敏处理。
 * 通过 {@link DesensitizeValueSerializerModifier} 自动检测并应用 {@link DesensitizeSerializer}。
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
