package com.sloth.boot.starter.es.config;

import com.sloth.boot.starter.es.core.EsTemplate;
import com.sloth.boot.starter.es.support.EsIndexNameResolver;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * ES 自动配置测试。
 */
class EsAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(EsAutoConfiguration.class))
        .withBean(ElasticsearchTemplate.class, () -> mock(ElasticsearchTemplate.class));

    @Test
    void should_register_beans_when_elasticsearch_on_classpath() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(EsTemplate.class);
            assertThat(context).hasSingleBean(EsIndexNameResolver.class);
            assertThat(context).hasSingleBean(EsProperties.class);
        });
    }

    @Test
    void should_not_register_when_disabled() {
        contextRunner
            .withPropertyValues("sloth.es.enabled=false")
            .run(context -> assertThat(context).doesNotHaveBean(EsTemplate.class));
    }

    @Test
    void should_allow_user_override() {
        contextRunner
            .withBean(EsTemplate.class, () -> mock(EsTemplate.class))
            .run(context -> assertThat(context).hasSingleBean(EsTemplate.class));
    }
}
