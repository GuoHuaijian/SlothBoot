package com.sloth.boot.starter.redis.limiter;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.sloth.boot.starter.redis.annotation.LimitType;
import com.sloth.boot.starter.redis.annotation.RateLimit;
import com.sloth.boot.common.context.UserContext;
import com.sloth.boot.common.exception.BizException;
import com.sloth.boot.common.exception.GlobalErrorCode;
import com.sloth.boot.common.util.ServletUtil;
import com.sloth.boot.common.util.SpelUtil;
import com.sloth.boot.starter.redis.config.RedisProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.io.IOException;
import java.util.Collections;

/**
 * 限流切面。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Aspect
public class RateLimiterAspect {

    /**
     * 限流脚本返回值：请求被限流拒绝。
     */
    private static final long RATE_LIMIT_EXCEEDED = 0L;

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisProperties redisProperties;
    private final DefaultRedisScript<Long> rateLimiterScript;

    /**
     * 构造限流切面。
     *
     * @param stringRedisTemplate     StringRedisTemplate
     * @param redisProperties         Redis 配置
     * @param rateLimiterScriptSource 限流 Lua 脚本资源
     */
    public RateLimiterAspect(StringRedisTemplate stringRedisTemplate, RedisProperties redisProperties,
                             ResourceScriptSource rateLimiterScriptSource) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.redisProperties = redisProperties;
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setResultType(Long.class);
        try {
            script.setScriptText(rateLimiterScriptSource.getScriptAsString());
        } catch (IOException ex) {
            throw BizException.of(GlobalErrorCode.INTERNAL_ERROR, "加载限流脚本失败");
        }
        this.rateLimiterScript = script;
    }

    /**
     * 执行限流切面逻辑。
     *
     * @param joinPoint 切点
     * @param rateLimit 限流注解
     * @return 方法执行结果
     * @throws Throwable 执行异常
     */
    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String key = buildRateLimitKey(joinPoint, rateLimit);
        Long result = stringRedisTemplate.execute(rateLimiterScript, Collections.singletonList(key),
            String.valueOf(System.currentTimeMillis()), String.valueOf(rateLimit.period()),
            String.valueOf(rateLimit.count()), IdUtil.fastSimpleUUID());
        if (result == null || result == RATE_LIMIT_EXCEEDED) {
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
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            suffix = SpelUtil.parse(joinPoint.getTarget(), signature.getMethod(), joinPoint.getArgs(), rateLimit.key(),
                joinPoint.getSignature().toShortString());
        } else {
            suffix = joinPoint.getSignature().toShortString();
        }
        return redisProperties.getKeyPrefix() + "rate_limit:" + suffix;
    }
}
