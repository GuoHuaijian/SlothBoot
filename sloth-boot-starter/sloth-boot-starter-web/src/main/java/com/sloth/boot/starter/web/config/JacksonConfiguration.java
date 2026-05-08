package com.sloth.boot.starter.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sloth.boot.common.util.JsonUtil;
import com.sloth.boot.common.util.jackson.JacksonConfigUtil;
import com.sloth.boot.starter.web.properties.SlothWebProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jackson 配置。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
public class JacksonConfiguration {

    /**
     * 构造函数。
     *
     * @param slothWebProperties Web 配置
     */
    public JacksonConfiguration(SlothWebProperties slothWebProperties) {
        // 预留后续基于配置切换序列化行为。
    }

    /**
     * 注册 ObjectMapper。
     *
     * @return ObjectMapper
     */
    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = JsonUtil.getObjectMapper().copy();
        JacksonConfigUtil.configureMapper(mapper);
        return mapper;
    }
}
