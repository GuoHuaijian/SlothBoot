package com.sloth.boot.starter.redis.limiter;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.sloth.boot.common.annotation.LimitType;
import com.sloth.boot.common.annotation.RateLimit;
import com.sloth.boot.common.context.UserContext;
import com.sloth.boot.common.exception.BizException;
import com.sloth.boot.common.exception.GlobalErrorCode;
import com.sloth.boot.common.util.ServletUtil;
import com.sloth.boot.starter.redis.config.RedisProperties;
import com.sloth.boot.starter.redis.support.SpelExpressionSupport;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;

/**
 * 限流切面。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Aspect
@RequiredArgsConstructor
public class RateLimiterAspect {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisProperties redisProperties;
    private final ResourceScriptSource rateLimiterScriptSource;

    /**
     * 执行限流切面逻辑。
     *
     * @param joinPoint        切点
     * @param rateLimit        限流注解
     * @return 方法执行结果
     * @throws Throwable 执行异常
     */
    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String key = buildRateLimitKey(joinPoint, rateLimit);
        Long result = stringRedisTemplate.execute(
                buildRedisScript(),
                Collections.singletonList(key),
                String.valueOf(System.currentTimeMillis()),
                String.valueOf(rateLimit.period()),
                String.valueOf(rateLimit.count()),
                IdUtil.fastSimpleUUID()
        );
        if (result == null || result == 0L) {
            throw BizException.of(GlobalErrorCode.TOO_MANY_REQUESTS.getCode(), rateLimit.message());
        }
        return joinPoint.proceed();
    }

    private String buildRateLimitKey(ProceedingJoinPoint joinPoint, RateLimit rateLimit) {
        String suffix;
        if (rateLimit.type() == LimitType.IP) {
            HttpServletRequest request = ServletUtil.getRequest();
            suffix = request == null ? "unknown-ip" : ServletUtil.getClientIp(request);
        } else if (rateLimit.type() == LimitType.USER) {
            suffix = String.valueOf(UserContext.getUserId());
        } else if (StrUtil.isNotBlank(rateLimit.key())) {
            suffix = SpelExpressionSupport.parse(joinPoint, rateLimit.key(), joinPoint.getSignature().toShortString());
        } else {
            suffix = joinPoint.getSignature().toShortString();
        }
        return redisProperties.getKeyPrefix() + "rate_limit:" + suffix;
    }

    private DefaultRedisScript<Long> buildRedisScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Long.class);
        try {
            redisScript.setScriptText(rateLimiterScriptSource.getScriptAsString());
        } catch (IOException ex) {
            throw BizException.of(GlobalErrorCode.INTERNAL_ERROR, "加载限流脚本失败");
        }
        return redisScript;
    }
}
