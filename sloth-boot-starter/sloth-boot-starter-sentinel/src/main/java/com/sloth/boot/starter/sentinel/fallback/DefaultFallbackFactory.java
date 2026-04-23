package com.sloth.boot.starter.sentinel.fallback;

import com.sloth.boot.common.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * 通用 Feign 降级工厂模板。
 *
 * @param <T> 接口类型
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
public abstract class DefaultFallbackFactory<T> implements FallbackFactory<T> {

    /**
     * 创建降级代理。
     *
     * @param cause 原始异常
     * @return 降级对象
     */
    @Override
    public T create(Throwable cause) {
        log.error("Feign 调用触发 Sentinel 降级, cause={}", cause == null ? null : cause.getMessage(), cause);
        return fallback(cause);
    }

    /**
     * 子类实现具体降级对象。
     *
     * @param cause 原始异常
     * @return 降级对象
     */
    protected abstract T fallback(Throwable cause);

    /**
     * 构造默认失败响应。
     *
     * @param <R> 响应泛型
     * @return 默认失败响应
     */
    protected <R> com.sloth.boot.common.result.R<R> defaultFailure() {
        return R.fail("服务暂不可用");
    }
}
