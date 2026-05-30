package com.sloth.boot.starter.idempotent.spi;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.time.Duration;
import java.util.List;

/**
 * 基于 Redis 的幂等存储实现。
 * <p>
 * 使用 Redis SET NX 实现原子加锁，Lua 脚本实现原子释放。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class RedisIdempotentStore implements IdempotentStore {

    private static final String RELEASE_LOCK_SCRIPT = "scripts/release_lock.lua";

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> releaseScript = buildReleaseScript();

    @Override
    public boolean tryAcquire(String key, String requestId, Duration timeout) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, requestId, timeout));
    }

    @Override
    public boolean release(String key, String requestId) {
        Long result = redisTemplate.execute(releaseScript, List.of(key), requestId);
        return result != null && result > 0;
    }

    @Override
    public boolean consumeToken(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    private static DefaultRedisScript<Long> buildReleaseScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource(RELEASE_LOCK_SCRIPT)));
        script.setResultType(Long.class);
        return script;
    }
}
