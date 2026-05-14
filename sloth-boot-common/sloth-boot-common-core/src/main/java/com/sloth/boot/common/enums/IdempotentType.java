package com.sloth.boot.common.enums;

/**
 * 幂等模式枚举。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public enum IdempotentType {

    /**
     * 分布式锁模式（默认）。
     * 通过 Redis SET NX 实现，同一时刻只允许一个请求执行。
     */
    LOCK,

    /**
     * Token 预检模式。
     * 客户端先获取 Token，提交时携带 Token 进行校验。
     */
    TOKEN
}
