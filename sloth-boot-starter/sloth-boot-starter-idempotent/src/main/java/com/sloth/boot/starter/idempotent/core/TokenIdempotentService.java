package com.sloth.boot.starter.idempotent.core;

import cn.hutool.core.util.IdUtil;
import com.sloth.boot.starter.idempotent.config.IdempotentProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

/**
 * Token 幂等服务。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class TokenIdempotentService {

    private static final String TOKEN_PREFIX = "token:";

    private final StringRedisTemplate stringRedisTemplate;
    private final IdempotentProperties idempotentProperties;

    /**
     * 创建幂等 Token。
     *
     * @return Token
     */
    public String createToken() {
        String token = IdUtil.nanoId();
        stringRedisTemplate.opsForValue().set(
                buildTokenKey(token),
                "1",
                Duration.ofSeconds(idempotentProperties.getTimeout())
        );
        return token;
    }

    /**
     * 校验并消费 Token。
     *
     * @param token Token
     * @return 是否通过
     */
    public boolean checkToken(String token) {
        return Boolean.TRUE.equals(stringRedisTemplate.delete(buildTokenKey(token)));
    }

    private String buildTokenKey(String token) {
        return idempotentProperties.getKeyPrefix() + TOKEN_PREFIX + token;
    }
}
