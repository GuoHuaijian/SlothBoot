package com.sloth.boot.starter.es.config;

import com.sloth.boot.starter.es.core.EsTemplate;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

/**
 * Elasticsearch 自动配置。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnClass(ElasticsearchOperations.class)
@ConditionalOnProperty(prefix = "sloth.es", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(EsProperties.class)
public class EsAutoConfiguration {

    /**
     * 注册 ES 模板。
     *
     * @param elasticsearchOperations Elasticsearch 操作类
     * @return ES 模板
     */
    @Bean
    @ConditionalOnMissingBean
    public EsTemplate esTemplate(ElasticsearchOperations elasticsearchOperations) {
        return new EsTemplate(elasticsearchOperations);
    }
}
