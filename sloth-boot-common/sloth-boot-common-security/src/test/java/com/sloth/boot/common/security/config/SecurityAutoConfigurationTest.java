package com.sloth.boot.common.security.config;

import com.sloth.boot.common.security.sign.SignProperties;
import com.sloth.boot.common.security.xss.XssProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SecurityAutoConfiguration 条件装配测试")
class SecurityAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner =
        new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(SecurityAutoConfiguration.class));

    @Test
    @DisplayName("默认配置下注册 XssProperties 和 SignProperties")
    void registersCoreBeansByDefault() {
        contextRunner.run(context -> {
            assertThat(context.getBeansOfType(XssProperties.class)).isNotEmpty();
            assertThat(context.getBeansOfType(SignProperties.class)).isNotEmpty();
        });
    }
}
