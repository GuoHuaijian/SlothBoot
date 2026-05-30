package com.sloth.boot.starter.sms.config;

import com.sloth.boot.starter.sms.core.AliyunSmsClient;
import com.sloth.boot.starter.sms.core.SmsClient;
import com.sloth.boot.starter.sms.core.SmsTemplate;
import com.sloth.boot.starter.sms.core.TencentSmsClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 短信自动配置测试。
 */
class SmsAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(SmsAutoConfiguration.class));

    @Test
    void should_register_aliyun_client_by_default() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(SmsClient.class);
            assertThat(context).getBean(SmsClient.class).isInstanceOf(AliyunSmsClient.class);
            assertThat(context).hasSingleBean(SmsTemplate.class);
            assertThat(context).hasSingleBean(SmsProperties.class);
        });
    }

    @Test
    void should_register_tencent_client_when_type_is_tencent() {
        contextRunner
            .withPropertyValues("sloth.sms.type=tencent")
            .run(context -> {
                assertThat(context).hasSingleBean(SmsClient.class);
                assertThat(context).getBean(SmsClient.class).isInstanceOf(TencentSmsClient.class);
            });
    }

    @Test
    void should_not_register_when_disabled() {
        contextRunner
            .withPropertyValues("sloth.sms.enabled=false")
            .run(context -> {
                assertThat(context).doesNotHaveBean(SmsClient.class);
                assertThat(context).doesNotHaveBean(SmsTemplate.class);
            });
    }

    @Test
    void should_allow_user_override() {
        contextRunner
            .withBean(SmsClient.class, () -> org.mockito.Mockito.mock(SmsClient.class))
            .run(context -> assertThat(context).hasSingleBean(SmsClient.class));
    }
}
