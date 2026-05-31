package com.sloth.boot.starter.es.config;

import com.sloth.boot.starter.es.core.EsTemplate;
import com.sloth.boot.starter.es.index.EsAliasManager;
import com.sloth.boot.starter.es.monitoring.EsSlowQueryLogger;
import com.sloth.boot.starter.es.support.EsIndexNameResolver;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

/**
 * Elasticsearch 自动配置。
 * <p>
 * 注册 EsTemplate、EsIndexNameResolver 等核心 Bean。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnClass(ElasticsearchTemplate.class)
@ConditionalOnProperty(prefix = "sloth.es", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(EsProperties.class)
public class EsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(ElasticsearchTemplate.class)
    public EsTemplate esTemplate(ElasticsearchTemplate elasticsearchTemplate, EsProperties esProperties) {
        return new EsTemplate(elasticsearchTemplate, esProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public EsIndexNameResolver esIndexNameResolver(EsProperties esProperties) {
        return new EsIndexNameResolver(esProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "sloth.es", name = "slow-query-threshold")
    public EsSlowQueryLogger esSlowQueryLogger(EsProperties esProperties) {
        return new EsSlowQueryLogger(esProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(ElasticsearchOperations.class)
    public EsAliasManager esAliasManager(ElasticsearchOperations elasticsearchOperations) {
        return new EsAliasManager(elasticsearchOperations);
    }
}