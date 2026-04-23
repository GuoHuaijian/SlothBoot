package com.sloth.boot.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分布式锁注解
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    /**
     * 锁 key，支持 SpEL 表达式
     */
    String key();

    /**
     * 等待时间，单位秒
     */
    long waitTime() default 3;

    /**
     * 持有时间，单位秒
     */
    long leaseTime() default 30;

    /**
     * 提示消息
     */
    String message() default "操作正在处理中，请稍后再试";
}
