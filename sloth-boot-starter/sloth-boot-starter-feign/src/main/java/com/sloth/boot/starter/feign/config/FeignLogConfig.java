package com.sloth.boot.starter.feign.config;

import feign.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Feign 日志配置。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
public class FeignLogConfig {

    /**
     * 配置 Feign 日志级别。
     *
     * @param environment Spring 环境
     * @return 日志级别
     */
    @Bean
    @ConditionalOnMissingBean
    public Logger.Level feignLoggerLevel(Environment environment) {
        for (String profile : environment.getActiveProfiles()) {
            if ("dev".equalsIgnoreCase(profile)) {
                return Logger.Level.FULL;
            }
        }
        return Logger.Level.BASIC;
    }
}
