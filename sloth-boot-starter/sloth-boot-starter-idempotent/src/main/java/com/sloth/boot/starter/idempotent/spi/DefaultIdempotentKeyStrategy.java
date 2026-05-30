package com.sloth.boot.starter.idempotent.spi;

import cn.hutool.core.util.StrUtil;
import com.sloth.boot.common.context.UserContext;
import com.sloth.boot.common.util.IpUtil;
import com.sloth.boot.common.util.ServletUtil;
import com.sloth.boot.common.util.SpelUtil;
import com.sloth.boot.starter.idempotent.annotation.Idempotent;
import com.sloth.boot.starter.idempotent.config.IdempotentProperties;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * 默认幂等键生成策略。
 * <p>
 * Key 规则：
 * <ul>
 *   <li>自定义 key（SpEL）：prefix + SpEL 解析结果</li>
 *   <li>默认：prefix + 方法签名 + ":" + 用户ID（未登录时使用客户端 IP）</li>
 * </ul>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class DefaultIdempotentKeyStrategy implements IdempotentKeyStrategy {

    private final IdempotentProperties properties;

    @Override
    public String buildKey(ProceedingJoinPoint joinPoint, Idempotent idempotent) {
        String prefix = properties.getKeyPrefix();
        if (StrUtil.isNotBlank(idempotent.key())) {
            return prefix + SpelUtil.parse(joinPoint.getTarget(),
                ((MethodSignature) joinPoint.getSignature()).getMethod(),
                joinPoint.getArgs(), idempotent.key(), idempotent.key());
        }
        Long userId = UserContext.getUserId();
        String userPart = userId != null ? String.valueOf(userId) : IpUtil.getClientIp(ServletUtil.getRequest());
        return prefix + joinPoint.getSignature().toShortString() + ":" + userPart;
    }
}
