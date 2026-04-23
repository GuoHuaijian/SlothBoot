package com.sloth.boot.starter.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * 通用降级工厂模板。
 *
 * @param <T> 接口类型
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
public abstract class AbstractFallbackFactory<T> implements FallbackFactory<T> {

    /**
     * 构建降级对象。
     *
     * @param cause 原始异常
     * @return 降级对象
     */
    @Override
    public T create(Throwable cause) {
        log.error("Feign 调用降级, cause={}", cause == null ? null : cause.getMessage(), cause);
        return doCreate(cause);
    }

    /**
     * 子类实现具体降级逻辑。
     *
     * @param cause 原始异常
     * @return 降级对象
     */
    protected abstract T doCreate(Throwable cause);
}
