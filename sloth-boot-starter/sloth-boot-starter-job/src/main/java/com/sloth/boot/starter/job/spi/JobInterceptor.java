package com.sloth.boot.starter.job.spi;

/**
 * Job 执行拦截器 SPI。
 * <p>
 * 在 Job 执行前后提供钩子，用于日志、指标、告警等横切关注点。
 * 多个拦截器按 {@link org.springframework.core.annotation.Order} 排序执行。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public interface JobInterceptor {

    /**
     * Job 执行前。
     *
     * @param handlerName 处理器名称
     */
    default void beforeExecute(String handlerName) {
    }

    /**
     * Job 执行后（无论成功或失败）。
     *
     * @param handlerName 处理器名称
     * @param success     是否成功
     * @param costTimeMs  耗时（毫秒）
     * @param throwable   异常（成功时为 null）
     */
    default void afterExecute(String handlerName, boolean success, long costTimeMs, Throwable throwable) {
    }
}
