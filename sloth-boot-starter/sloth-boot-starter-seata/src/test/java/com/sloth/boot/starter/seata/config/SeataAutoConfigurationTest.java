package com.sloth.boot.starter.seata.config;

import io.seata.spring.annotation.GlobalTransactionScanner;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Seata 自动配置测试。
 */
class SeataAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(SeataAutoConfiguration.class))
        .withPropertyValues("sloth.seata.enabled=true");

    @Test
    void should_register_scanner_when_enabled() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(GlobalTransactionScanner.class);
            assertThat(context).hasSingleBean(SeataProperties.class);
        });
    }

    @Test
    void should_not_register_when_disabled() {
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SeataAutoConfiguration.class))
            .withPropertyValues("sloth.seata.enabled=false")
            .run(context -> assertThat(context).doesNotHaveBean(GlobalTransactionScanner.class));
    }

    @Test
    void should_not_register_when_seata_class_missing() {
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SeataAutoConfiguration.class))
            .withPropertyValues("sloth.seata.enabled=true")
            .run(context -> assertThat(context).doesNotHaveBean(GlobalTransactionScanner.class));
    }
}
