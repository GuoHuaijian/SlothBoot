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
    @DisplayName("默认配置下注册核心 Bean")
    void registersCoreBeansByDefault() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(XssProperties.class);
            assertThat(context).hasSingleBean(SignProperties.class);
        });
    }

    @Test
    @DisplayName("sloth.xss.enabled=false 时不注册 XssProperties")
    void xssDisabledByProperty() {
        contextRunner.withPropertyValues("sloth.xss.enabled=false").run(context -> {
            assertThat(context).doesNotHaveBean(XssProperties.class);
            // SignProperties 不受影响
            assertThat(context).hasSingleBean(SignProperties.class);
        });
    }

    @Test
    @DisplayName("sloth.sign.enabled=false 时不注册 SignProperties")
    void signDisabledByProperty() {
        contextRunner.withPropertyValues("sloth.sign.enabled=false").run(context -> {
            assertThat(context).doesNotHaveBean(SignProperties.class);
            // XssProperties 不受影响
            assertThat(context).hasSingleBean(XssProperties.class);
        });
    }

    @Test
    @DisplayName("用户自定义 XssProperties 可覆盖默认")
    void customXssPropertiesOverrides() {
        XssProperties custom = new XssProperties();
        custom.setCleanHtml(false);
        contextRunner.withBean("customXss", XssProperties.class, () -> custom).run(context -> {
            assertThat(context).hasSingleBean(XssProperties.class);
            assertThat(context.getBean(XssProperties.class).isCleanHtml()).isFalse();
        });
    }
}
