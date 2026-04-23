package com.sloth.boot.starter.redis.idempotent;

import cn.hutool.core.util.StrUtil;
import com.sloth.boot.common.annotation.Idempotent;
import com.sloth.boot.common.context.UserContext;
import com.sloth.boot.common.exception.BizException;
import com.sloth.boot.common.exception.GlobalErrorCode;
import com.sloth.boot.starter.redis.config.RedisProperties;
import com.sloth.boot.starter.redis.support.SpelExpressionSupport;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.UUID;

/**
 * 幂等切面。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Aspect
@RequiredArgsConstructor
public class IdempotentAspect {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisProperties redisProperties;

    /**
     * 执行幂等控制逻辑。
     *
     * @param joinPoint  切点
     * @param idempotent 幂等注解
     * @return 方法执行结果
     * @throws Throwable 执行异常
     */
    @Around("@annotation(idempotent)")
    public Object around(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        String key = buildIdempotentKey(joinPoint, idempotent);
        String value = UUID.randomUUID().toString();
        Boolean success = stringRedisTemplate.opsForValue()
                .setIfAbsent(key, value, Duration.ofSeconds(idempotent.timeout()));
        if (!Boolean.TRUE.equals(success)) {
            throw BizException.of(GlobalErrorCode.REPEATED_REQUEST.getCode(), idempotent.message());
        }
        try {
            return joinPoint.proceed();
        } catch (Throwable ex) {
            String cached = stringRedisTemplate.opsForValue().get(key);
            if (StrUtil.equals(value, cached)) {
                stringRedisTemplate.delete(key);
            }
            throw ex;
        }
    }

    private String buildIdempotentKey(ProceedingJoinPoint joinPoint, Idempotent idempotent) {
        String suffix = SpelExpressionSupport.parse(
                joinPoint,
                idempotent.key(),
                joinPoint.getSignature().toShortString() + ":" + UserContext.getUserId()
        );
        return redisProperties.getKeyPrefix() + "idempotent:" + suffix;
    }
}
