package com.sloth.boot.starter.redis.config;

import com.sloth.boot.starter.redis.core.RedisCacheUtil;
import com.sloth.boot.starter.redis.limiter.RateLimiterAspect;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("RedisAutoConfiguration 条件装配测试")
class RedisAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(
            AutoConfigurations.of(org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration.class,
                RedisAutoConfiguration.class))
        .withBean(RedisConnectionFactory.class, () -> mock(RedisConnectionFactory.class));

    @Test
    @DisplayName("默认配置下注册核心 Bean")
    void registersCoreBeansByDefault() {
        contextRunner.run(context -> {
            assertThat(context).hasBean("slothRedisTemplate");
            assertThat(context.getBeansOfType(RedisCacheUtil.class)).isNotEmpty();
            assertThat(context.getBeansOfType(RateLimiterAspect.class)).isNotEmpty();
        });
    }

    @Test
    @DisplayName("sloth.redis.enabled=false 时不注册任何 Bean")
    void disabledByProperty() {
        contextRunner.withPropertyValues("sloth.redis.enabled=false").run(context -> {
            assertThat(context).doesNotHaveBean(RedisCacheUtil.class);
            assertThat(context).doesNotHaveBean(RateLimiterAspect.class);
        });
    }

    @Test
    @DisplayName("用户自定义 RedisCacheUtil 可覆盖默认")
    void customRedisCacheUtilOverrides() {
        contextRunner.withBean("customRedisCacheUtil", RedisCacheUtil.class, () -> mock(RedisCacheUtil.class))
            .run(context -> {
                assertThat(context.getBeansOfType(RedisCacheUtil.class)).isNotEmpty();
            });
    }
}
