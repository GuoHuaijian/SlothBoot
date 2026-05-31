package com.sloth.boot.starter.web.config;

import com.sloth.boot.common.util.jackson.JacksonConfigUtil;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.JacksonModule;

/**
 * Jackson 自动配置。
 * <p>
 * 注册自定义序列化器/反序列化器模块，由 Spring Boot 自动注入到 ObjectMapper。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnClass(JacksonModule.class)
public class JacksonAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public JacksonModule slothCustomSerializersModule() {
        return JacksonConfigUtil.createCustomSerializersModule();
    }
}
