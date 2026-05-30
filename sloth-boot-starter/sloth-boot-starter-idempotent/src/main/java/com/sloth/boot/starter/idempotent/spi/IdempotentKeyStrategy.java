package com.sloth.boot.starter.idempotent.spi;

import com.sloth.boot.starter.idempotent.annotation.Idempotent;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * 幂等键生成策略 SPI。
 * <p>
 * 定义幂等键的生成规则。默认实现使用方法签名 + 用户标识。
 * 业务方可提供自定义实现覆盖默认 Bean。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public interface IdempotentKeyStrategy {

    /**
     * 构建幂等键。
     *
     * @param joinPoint  切点
     * @param idempotent 幂等注解
     * @return 幂等键
     */
    String buildKey(ProceedingJoinPoint joinPoint, Idempotent idempotent);
}
