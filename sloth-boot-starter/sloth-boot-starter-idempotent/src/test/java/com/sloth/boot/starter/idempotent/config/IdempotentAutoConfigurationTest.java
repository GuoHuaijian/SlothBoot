package com.sloth.boot.starter.idempotent.config;

import com.sloth.boot.starter.idempotent.aspect.IdempotentAspect;
import com.sloth.boot.starter.idempotent.core.TokenIdempotentService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * 幂等自动配置测试。
 */
class IdempotentAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(IdempotentAutoConfiguration.class))
        .withBean(StringRedisTemplate.class, () -> mock(StringRedisTemplate.class));

    @Test
    void should_register_beans_when_enabled() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(TokenIdempotentService.class);
            assertThat(context).hasSingleBean(IdempotentAspect.class);
            assertThat(context).hasSingleBean(IdempotentProperties.class);
        });
    }

    @Test
    void should_not_register_when_disabled() {
        contextRunner
            .withPropertyValues("sloth.idempotent.enabled=false")
            .run(context -> {
                assertThat(context).doesNotHaveBean(TokenIdempotentService.class);
                assertThat(context).doesNotHaveBean(IdempotentAspect.class);
            });
    }

    @Test
    void should_not_register_when_redis_template_missing() {
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(IdempotentAutoConfiguration.class))
            .run(context -> {
                assertThat(context).doesNotHaveBean(TokenIdempotentService.class);
                assertThat(context).doesNotHaveBean(IdempotentAspect.class);
            });
    }

    @Test
    void should_allow_user_override() {
        contextRunner
            .withBean(TokenIdempotentService.class,
                () -> mock(TokenIdempotentService.class))
            .run(context -> {
                assertThat(context).hasSingleBean(TokenIdempotentService.class);
            });
    }
}
