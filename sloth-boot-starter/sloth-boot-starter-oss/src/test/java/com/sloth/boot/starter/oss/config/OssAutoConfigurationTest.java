package com.sloth.boot.starter.oss.config;

import com.sloth.boot.starter.oss.core.OssClient;
import com.sloth.boot.starter.oss.core.OssTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * OSS 自动配置测试。
 */
class OssAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(OssAutoConfiguration.class));

    @Test
    void should_register_local_oss_client_by_default() {
        contextRunner.run(context -> {
            assertThat(context.getBean("ossClient")).isInstanceOf(OssClient.class);
            assertThat(context).hasSingleBean(OssTemplate.class);
            assertThat(context).hasSingleBean(OssProperties.class);
        });
    }

    @Test
    void should_allow_user_override() {
        contextRunner
            .withBean("customOssClient", OssClient.class, () -> new com.sloth.boot.starter.oss.core.LocalOssClient(new OssProperties()))
            .run(context -> assertThat(context.getBean("customOssClient")).isInstanceOf(OssClient.class));
    }
}
