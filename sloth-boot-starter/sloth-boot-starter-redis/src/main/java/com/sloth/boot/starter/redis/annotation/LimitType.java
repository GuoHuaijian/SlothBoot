package com.sloth.boot.starter.redis.annotation;

/**
 * 限流类型
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public enum LimitType {

    /**
     * 默认限流
     */
    DEFAULT,

    /**
     * 基于 IP 限流
     */
    IP,

    /**
     * 基于用户限流
     */
    USER
}
