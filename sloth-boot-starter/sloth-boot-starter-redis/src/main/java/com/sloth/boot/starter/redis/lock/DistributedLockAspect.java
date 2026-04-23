package com.sloth.boot.starter.redis.lock;

import cn.hutool.core.util.StrUtil;
import com.sloth.boot.common.annotation.DistributedLock;
import com.sloth.boot.common.exception.BizException;
import com.sloth.boot.common.exception.GlobalErrorCode;
import com.sloth.boot.starter.redis.config.RedisProperties;
import com.sloth.boot.starter.redis.support.SpelExpressionSupport;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁切面。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Aspect
@RequiredArgsConstructor
public class DistributedLockAspect {

    private final DistributedLock distributedLock;
    private final RedisProperties redisProperties;

    /**
     * 执行分布式锁切面逻辑。
     *
     * @param joinPoint       切点
     * @param distributedLockAnnotation 分布式锁注解
     * @return 方法执行结果
     * @throws Throwable 执行异常
     */
    @Around("@annotation(distributedLockAnnotation)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedLock distributedLockAnnotation) throws Throwable {
        String defaultKey = joinPoint.getSignature().toShortString();
        String key = SpelExpressionSupport.parse(joinPoint, distributedLockAnnotation.key(), defaultKey);
        if (StrUtil.isBlank(key)) {
            key = defaultKey;
        }
        if (!key.startsWith(redisProperties.getKeyPrefix())) {
            key = redisProperties.getKeyPrefix() + "lock:" + key;
        }
        boolean locked = distributedLock.tryLock(
                key,
                distributedLockAnnotation.waitTime() > 0 ? distributedLockAnnotation.waitTime() : redisProperties.getLockWaitTime(),
                distributedLockAnnotation.leaseTime() > 0 ? distributedLockAnnotation.leaseTime() : redisProperties.getLockLeaseTime(),
                TimeUnit.SECONDS
        );
        if (!locked) {
            throw BizException.of(GlobalErrorCode.REPEATED_REQUEST.getCode(), distributedLockAnnotation.message());
        }
        try {
            return joinPoint.proceed();
        } finally {
            distributedLock.unlock(key);
        }
    }
}
