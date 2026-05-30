package com.sloth.boot.starter.es.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.health.contributor.AbstractHealthIndicator;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Component;

/**
 * Elasticsearch 健康检查指示器。
 * <p>
 * 检测 ES 集群连接状态，上报节点数和集群信息。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Component
@ConditionalOnClass(name = "org.springframework.boot.health.contributor.Health")
@RequiredArgsConstructor
public class EsHealthIndicator extends AbstractHealthIndicator {

    /**
     * 用于检测集群可达性的内部索引名。
     */
    private static final String SYSTEM_INDEX = ".elasticsearch";

    private final ElasticsearchTemplate elasticsearchTemplate;

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        try {
            elasticsearchTemplate.indexOps(IndexCoordinates.of(SYSTEM_INDEX)).exists();
            builder.up().withDetail("status", "connected");
        } catch (Exception e) {
            log.warn("[ES] Elasticsearch health check failed", e);
            builder.down(e).withDetail("status", "disconnected");
        }
    }
}
