package com.sloth.boot.starter.es.monitoring;

import com.sloth.boot.starter.es.config.EsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * ES 慢查询日志 AOP。
 * <p>
 * 拦截 {@link com.sloth.boot.starter.es.core.EsTemplate} 中耗时超过阈值的方法，打印 WARN 日志。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Aspect
@ConditionalOnClass(name = "org.aspectj.lang.ProceedingJoinPoint")
@ConditionalOnProperty(prefix = "sloth.es", name = "slow-query-threshold")
@RequiredArgsConstructor
public class EsSlowQueryLogger {

    /**
     * 毫秒与秒的转换系数。
     */
    private static final long MILLIS_PER_SECOND = 1000L;

    private final EsProperties esProperties;

    /**
     * 拦截 EsTemplate 所有 public 方法。
     */
    @Around("execution(public * com.sloth.boot.starter.es.core.EsTemplate.*(..))")
    public Object logSlowQuery(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            long thresholdMs = esProperties.getSlowQueryThreshold() * MILLIS_PER_SECOND;
            if (elapsed > thresholdMs) {
                log.warn("[ES Slow Query] method={}, args={}, elapsed={}ms, threshold={}s",
                    joinPoint.getSignature().toShortString(),
                    joinPoint.getArgs(),
                    elapsed,
                    esProperties.getSlowQueryThreshold());
            }
        }
    }
}
