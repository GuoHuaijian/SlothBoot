package com.sloth.boot.starter.idempotent.aspect;

import cn.hutool.core.util.StrUtil;
import com.sloth.boot.common.annotation.Idempotent;
import com.sloth.boot.common.context.UserContext;
import com.sloth.boot.common.exception.BizException;
import com.sloth.boot.common.exception.GlobalErrorCode;
import com.sloth.boot.starter.idempotent.config.IdempotentProperties;
import com.sloth.boot.starter.idempotent.support.SpelKeyResolver;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.UUID;

/**
 * 幂等切面增强。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Aspect
@RequiredArgsConstructor
public class IdempotentAspect {

    private final StringRedisTemplate stringRedisTemplate;
    private final IdempotentProperties idempotentProperties;
    private final SpelKeyResolver spelKeyResolver;

    /**
     * 执行幂等逻辑。
     *
     * @param joinPoint  切点
     * @param idempotent 幂等注解
     * @return 方法执行结果
     * @throws Throwable 执行异常
     */
    @Around("@annotation(idempotent)")
    public Object around(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        String key = buildIdempotentKey(joinPoint, idempotent);
        String requestId = UUID.randomUUID().toString();
        int timeout = idempotent.timeout() > 0 ? idempotent.timeout() : idempotentProperties.getTimeout();
        Boolean locked = stringRedisTemplate.opsForValue().setIfAbsent(key, requestId, Duration.ofSeconds(timeout));
        if (!Boolean.TRUE.equals(locked)) {
            throw BizException.of(GlobalErrorCode.REPEATED_REQUEST.getCode(), idempotent.message());
        }
        try {
            return joinPoint.proceed();
        } catch (Throwable ex) {
            String currentValue = stringRedisTemplate.opsForValue().get(key);
            if (StrUtil.equals(requestId, currentValue)) {
                stringRedisTemplate.delete(key);
            }
            throw ex;
        }
    }

    private String buildIdempotentKey(ProceedingJoinPoint joinPoint, Idempotent idempotent) {
        String defaultKey = joinPoint.getSignature().toShortString() + ":" + UserContext.getUserId();
        String resolved = spelKeyResolver.resolve(joinPoint, idempotent.key(), defaultKey);
        return idempotentProperties.getKeyPrefix() + resolved;
    }
}
