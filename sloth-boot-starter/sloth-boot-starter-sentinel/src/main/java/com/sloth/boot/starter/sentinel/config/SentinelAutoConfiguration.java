package com.sloth.boot.starter.sentinel.config;

import com.alibaba.csp.sentinel.adapter.spring.webmvc_v6x.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.adapter.spring.webmvc_v6x.callback.WebCallbackManager;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.sloth.boot.starter.sentinel.datasource.NacosDataSourceConfig;
import com.sloth.boot.starter.sentinel.handler.GlobalBlockHandler;
import com.sloth.boot.starter.sentinel.handler.SentinelBlockExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * Sentinel 自动配置。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnClass(BlockException.class)
@ConditionalOnProperty(prefix = "sloth.sentinel", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(SentinelProperties.class)
public class SentinelAutoConfiguration {

    /**
     * 注册全局 Block 处理器。
     *
     * @return 全局 Block 处理器
     */
    @Bean
    @ConditionalOnMissingBean
    public GlobalBlockHandler globalBlockHandler() {
        return new GlobalBlockHandler();
    }

    /**
     * 注册 Sentinel Web Block 异常处理器。
     *
     * @param globalBlockHandler 全局处理器
     * @return Web Block 异常处理器
     */
    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnClass(BlockExceptionHandler.class)
    @ConditionalOnMissingBean
    public SentinelBlockExceptionHandler sentinelBlockExceptionHandler(GlobalBlockHandler globalBlockHandler) {
        SentinelBlockExceptionHandler handler = new SentinelBlockExceptionHandler(globalBlockHandler);
        WebCallbackManager.setBlockHandler(handler);
        return handler;
    }

    /**
     * 注册 Nacos 数据源配置。
     *
     * @param sentinelProperties 配置属性
     * @param environment        Spring 环境
     * @return Nacos 数据源配置
     */
    @Bean
    @ConditionalOnClass(NacosDataSource.class)
    @ConditionalOnProperty(prefix = "sloth.sentinel", name = "datasource", havingValue = "nacos", matchIfMissing = true)
    @ConditionalOnMissingBean
    public NacosDataSourceConfig nacosDataSourceConfig(SentinelProperties sentinelProperties, Environment environment) {
        return new NacosDataSourceConfig(sentinelProperties, environment);
    }
}
