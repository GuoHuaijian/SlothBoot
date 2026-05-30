package com.sloth.boot.starter.idempotent.aspect;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.sloth.boot.starter.idempotent.annotation.Idempotent;
import com.sloth.boot.starter.idempotent.annotation.IdempotentType;
import com.sloth.boot.common.exception.BizException;
import com.sloth.boot.common.exception.GlobalErrorCode;
import com.sloth.boot.common.util.SpelUtil;
import com.sloth.boot.starter.idempotent.config.IdempotentProperties;
import com.sloth.boot.starter.idempotent.core.TokenIdempotentService;
import com.sloth.boot.starter.idempotent.spi.IdempotentKeyStrategy;
import com.sloth.boot.starter.web.util.ServletUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.time.Duration;
import java.util.List;

/**
 * 幂等切面，拦截 {@code @Idempotent} 注解的方法。
 * <p>
 * 支持两种幂等模式：
 * <ul>
 *   <li>{@link IdempotentType#LOCK} — 分布式锁模式（默认），通过 Redis SET NX 实现原子加锁</li>
 *   <li>{@link IdempotentType#TOKEN} — Token 预检模式，客户端先获取 Token，提交时携带校验</li>
 * </ul>
 * <p>
 * 锁模式下，异常路径通过 Lua 脚本原子释放锁，避免误删其他请求持有的锁。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Aspect
public class IdempotentAspect {

    /**
     * 原子释放锁的 Lua 脚本。
     * <p>
     * 仅当 Redis 中当前值等于 requestId 时才执行 DEL，防止误删其他请求的锁。
     */
    private static final String RELEASE_LOCK_SCRIPT = "scripts/release_lock.lua";

    private final StringRedisTemplate redisTemplate;
    private final IdempotentProperties idempotentProperties;
    private final TokenIdempotentService tokenIdempotentService;
    private final IdempotentKeyStrategy idempotentKeyStrategy;
    private final DefaultRedisScript<Long> releaseScript;

    public IdempotentAspect(StringRedisTemplate redisTemplate,
                            IdempotentProperties idempotentProperties,
                            TokenIdempotentService tokenIdempotentService,
                            IdempotentKeyStrategy idempotentKeyStrategy) {
        this.redisTemplate = redisTemplate;
        this.idempotentProperties = idempotentProperties;
        this.tokenIdempotentService = tokenIdempotentService;
        this.idempotentKeyStrategy = idempotentKeyStrategy;
        this.releaseScript = buildReleaseScript();
    }

    /**
     * 幂等拦截入口。
     * <p>
     * 根据 {@link Idempotent#type()} 分发到锁模式或 Token 模式处理。
     *
     * @param joinPoint  切点
     * @param idempotent 幂等注解
     * @return 方法执行结果
     * @throws Throwable 业务异常或幂等拦截异常
     */
    @Around("@annotation(idempotent)")
    public Object around(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        if (idempotent.type() == IdempotentType.TOKEN) {
            return handleTokenMode(joinPoint, idempotent);
        }
        return handleLockMode(joinPoint, idempotent);
    }

    /**
     * 分布式锁模式处理。
     * <p>
     * 流程：
     * <ol>
     *   <li>构建幂等 Key（支持 SpEL 自定义或默认方法签名+用户标识）</li>
     *   <li>尝试 SET NX 获取锁（支持 waitTime 等待重试）</li>
     *   <li>获取成功则执行方法，异常时通过 Lua 脚本原子释放锁</li>
     *   <li>获取失败则抛出 {@link BizException}，消息支持 SpEL 动态解析</li>
     * </ol>
     */
    private Object handleLockMode(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        String key = idempotentKeyStrategy.buildKey(joinPoint, idempotent);
        String requestId = IdUtil.nanoId();
        int timeout = idempotent.timeout() > 0 ? idempotent.timeout() : idempotentProperties.getTimeout();
        Duration duration = Duration.ofSeconds(timeout);

        // 尝试获取锁
        boolean acquired = tryAcquireLock(key, requestId, duration, idempotent.waitTime());

        if (!acquired) {
            String message = SpelUtil.parse(joinPoint.getTarget(), ((MethodSignature) joinPoint.getSignature()).getMethod(),
                    joinPoint.getArgs(), idempotent.message(), idempotent.message());
            log.debug("[Idempotent] 幂等拦截: key={}", key);
            throw BizException.of(GlobalErrorCode.REPEATED_REQUEST, message);
        }

        try {
            return joinPoint.proceed();
        } catch (Throwable ex) {
            // 执行异常时原子释放锁，仅当锁仍为当前请求持有时才删除
            log.debug("[Idempotent] 执行异常，释放幂等锁: key={}", key);
            redisTemplate.execute(releaseScript, List.of(key), requestId);
            throw ex;
        }
    }

    /**
     * 尝试获取分布式锁。
     *
     * @param key       Redis key
     * @param requestId 请求唯一标识（NanoId）
     * @param duration  锁过期时间
     * @param waitTime  等待时间（秒），0 表示不等待
     * @return true 获取成功，false 获取失败
     */
    private boolean tryAcquireLock(String key, String requestId, Duration duration, int waitTime) {
        if (waitTime > 0) {
            // 等待重试模式：循环尝试直到超时
            long deadline = System.currentTimeMillis() + waitTime * 1000L;
            while (System.currentTimeMillis() < deadline) {
                if (Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, requestId, duration))) {
                    return true;
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
            return false;
        }
        // 非等待模式：单次尝试
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, requestId, duration));
    }

    /**
     * Token 预检模式处理。
     * <p>
     * 从方法参数或 HTTP 请求参数中提取 Token，调用 {@link TokenIdempotentService#checkToken} 校验。
     * Token 使用一次即失效（Redis 原子删除）。
     */
    private Object handleTokenMode(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        String token = extractToken(joinPoint, idempotent.tokenParam());
        if (StrUtil.isBlank(token) || tokenIdempotentService == null || !tokenIdempotentService.checkToken(token)) {
            String message = SpelUtil.parse(joinPoint.getTarget(), ((MethodSignature) joinPoint.getSignature()).getMethod(),
                    joinPoint.getArgs(), idempotent.message(), idempotent.message());
            log.debug("[Idempotent] 幂等令牌校验失败: token={}", token);
            throw BizException.of(GlobalErrorCode.REPEATED_REQUEST, message);
        }
        return joinPoint.proceed();
    }

    /**
     * 从方法参数或 HTTP 请求参数中提取 Token。
     * <p>
     * 优先匹配方法参数名，其次从 HttpServletRequest 参数中获取。
     *
     * @param joinPoint  切点
     * @param tokenParam Token 参数名
     * @return Token 值，未找到返回 null
     */
    private String extractToken(ProceedingJoinPoint joinPoint, String tokenParam) {
        // 优先从方法参数中查找
        Object[] args = joinPoint.getArgs();
        String[] paramNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        for (int i = 0; i < paramNames.length; i++) {
            if (tokenParam.equals(paramNames[i]) && args[i] instanceof String str) {
                return str;
            }
        }
        // 降级到 HTTP 请求参数
        String param = ServletUtil.getRequestParam(tokenParam);
        return StrUtil.blankToDefault(param, null);
    }

    /**
     * 从 classpath 加载原子释放锁 Lua 脚本。
     */
    private static DefaultRedisScript<Long> buildReleaseScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource(RELEASE_LOCK_SCRIPT)));
        script.setResultType(Long.class);
        return script;
    }
}
