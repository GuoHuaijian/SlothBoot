package com.sloth.boot.common.log.config;

import com.sloth.boot.common.log.OperateLogHandler;
import com.sloth.boot.common.log.aspect.OperateLogAspect;
import com.sloth.boot.common.log.event.OperateLogListener;
import com.sloth.boot.common.log.filter.RequestLogFilter;
import com.sloth.boot.common.log.filter.TraceFilter;
import com.sloth.boot.common.log.properties.LogProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * 日志自动配置。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = "sloth.log", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(LogProperties.class)
public class LogAutoConfiguration {

    /**
     * 注册 Trace 过滤器。
     *
     * @return Trace 过滤器
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ConditionalOnMissingBean
    public TraceFilter traceFilter() {
        return new TraceFilter();
    }

    /**
     * 注册请求日志过滤器。
     *
     * @param logProperties 日志配置
     * @return 请求日志过滤器
     */
    @Bean
    @ConditionalOnMissingBean
    public RequestLogFilter requestLogFilter(LogProperties logProperties) {
        return new RequestLogFilter(logProperties);
    }

    /**
     * 注册操作日志切面。
     *
     * @param eventPublisher 事件发布器
     * @return 操作日志切面
     */
    @Bean
    @ConditionalOnMissingBean
    public OperateLogAspect operateLogAspect(org.springframework.context.ApplicationEventPublisher eventPublisher) {
        return new OperateLogAspect(eventPublisher);
    }

    /**
     * 注册默认操作日志处理器。
     *
     * @return 操作日志处理器
     */
    @Bean
    @ConditionalOnMissingBean
    public OperateLogHandler operateLogHandler() {
        return dto -> {
            // 默认空实现，交由监听器记录日志，业务方可自行覆盖。
        };
    }

    /**
     * 注册操作日志监听器。
     *
     * @param logProperties     日志配置
     * @param operateLogHandler 处理器
     * @return 监听器
     */
    @Bean
    @ConditionalOnMissingBean
    public OperateLogListener operateLogListener(LogProperties logProperties, OperateLogHandler operateLogHandler) {
        return new OperateLogListener(logProperties, operateLogHandler);
    }
}
