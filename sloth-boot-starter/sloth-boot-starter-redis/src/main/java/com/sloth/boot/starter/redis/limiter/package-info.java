/**
 * 接口限流实现。
 * <p>
 * 基于 Redis + Lua 脚本的滑动窗口限流，提供注解驱动方式， 支持 IP 维度、用户维度和自定义 Key 维度限流。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
package com.sloth.boot.starter.redis.limiter;
