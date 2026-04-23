package com.sloth.boot.starter.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sloth.boot.common.util.JsonUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

/**
 * Redis 序列化配置。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
public class RedisSerializerConfig {

    /**
     * 注册通用 Redis JSON 序列化器。
     *
     * @param redisProperties Redis 配置
     * @return Redis JSON 序列化器
     */
    @Bean
    @ConditionalOnMissingBean
    public GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer(RedisProperties redisProperties) {
        ObjectMapper objectMapper = JsonUtil.getObjectMapper().copy();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        if (redisProperties.isEnableTypeInfo()) {
            objectMapper.activateDefaultTyping(
                    BasicPolymorphicTypeValidator.builder()
                            .allowIfSubType(Object.class)
                            .build(),
                    ObjectMapper.DefaultTyping.NON_FINAL
            );
        }
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }
}
