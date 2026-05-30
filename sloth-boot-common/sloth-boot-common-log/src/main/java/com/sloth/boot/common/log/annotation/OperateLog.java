package com.sloth.boot.common.log.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解。
 * <p>
 * 标注在 Controller 方法上，自动采集操作日志。
 * 通过 {@link com.sloth.boot.common.log.aspect.OperateLogAspect} 实现 AOP 拦截。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperateLog {

    /**
     * 模块名
     */
    String module() default "";

    /**
     * 操作描述
     */
    String description() default "";

    /**
     * 操作类型
     */
    OperateTypeEnum type() default OperateTypeEnum.OTHER;

    /**
     * 是否保存请求数据
     */
    boolean saveRequestData() default true;

    /**
     * 是否保存响应数据
     */
    boolean saveResponseData() default true;
}
