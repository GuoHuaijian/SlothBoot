package com.sloth.boot.example.controller;

import com.sloth.boot.common.annotation.DistributedLock;
import com.sloth.boot.common.annotation.Idempotent;
import com.sloth.boot.common.annotation.RateLimit;
import com.sloth.boot.common.result.R;
import com.sloth.boot.starter.redis.core.RedisCacheUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

/**
 * Redis 能力示例控制器。
 * <p>
 * 展示缓存工具、分布式锁、限流、幂等注解的使用方式。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class RedisExampleController {

    private final RedisCacheUtil redisCacheUtil;

    /**
     * 缓存写入示例
     */
    @PostMapping("/cache/set")
    public R<String> setCache(@RequestParam String key, @RequestParam String value) {
        redisCacheUtil.set(key, value, Duration.ofSeconds(60));
        return R.ok("写入成功");
    }

    /**
     * 缓存读取示例
     */
    @GetMapping("/cache/get")
    public R<String> getCache(@RequestParam String key) {
        return R.ok(redisCacheUtil.get(key, String.class));
    }

    /**
     * 分布式锁示例
     * <p>
     * 使用 @DistributedLock 注解，自动获取/释放 Redisson 分布式锁。 key 支持 SpEL 表达式，可引用方法参数。
     */
    @PostMapping("/lock/{orderId}")
    @DistributedLock(key = "'order:' + #orderId", waitTime = 3, leaseTime = 30, message = "请勿重复操作")
    public R<String> processOrder(@PathVariable String orderId) {
        // 模拟业务处理
        return R.ok("订单 " + orderId + " 处理完成");
    }

    /**
     * 限流示例
     * <p>
     * 使用 @RateLimit 注解，基于 Redis 滑动窗口限流。 以下示例：每 10 秒最多允许 5 次请求。
     */
    @GetMapping("/rate-limit")
    @RateLimit(count = 5, period = 10, message = "请求过于频繁，请稍后再试")
    public R<String> rateLimitedApi() {
        return R.ok("请求成功");
    }

    /**
     * 幂等示例
     * <p>
     * 使用 @Idempotent 注解，防止重复提交。 同一请求在 timeout 秒内只能执行一次。
     */
    @PostMapping("/idempotent")
    @Idempotent(timeout = 30, message = "请勿重复提交")
    public R<String> idempotentApi(@RequestBody Map<String, String> body) {
        return R.ok("提交成功: " + body.get("data"));
    }
}
