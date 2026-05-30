package com.sloth.boot.starter.monitor.config;

import com.sloth.boot.starter.monitor.tracing.TraceContextBridge;
import com.sloth.boot.starter.monitor.config.TracingProperties;
import io.micrometer.tracing.Tracer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 链路追踪自动配置。
 * <p>
 * 当类路径中存在 {@code io.micrometer.tracing.Tracer} 且未显式禁用时自动激活，
 * 提供 TraceContext 与 Micrometer Tracing 之间的桥接能力。
 * <p>
 * 配置项前缀: {@code sloth.monitor.tracing.*}
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnClass(name = "io.micrometer.tracing.Tracer")
@ConditionalOnProperty(prefix = "sloth.monitor", name = "tracing-enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(TracingProperties.class)
public class TracingAutoConfiguration {

    /**
     * 注册 TraceContext 与 Micrometer Tracing 的桥接器。
     * <p>
     * 从 Micrometer Tracer 获取当前 span 的 traceId/spanId，
     * 同步回自定义的 {@link com.sloth.boot.common.context.TraceContext}，
     * 确保异步线程和日志中保持统一的追踪标识。
     *
     * @param tracer Micrometer Tracer
     * @return TraceContextBridge 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public TraceContextBridge traceContextBridge(Tracer tracer) {
        return new TraceContextBridge(tracer);
    }
}
