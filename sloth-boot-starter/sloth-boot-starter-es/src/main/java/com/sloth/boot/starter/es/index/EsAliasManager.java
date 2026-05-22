package com.sloth.boot.starter.es.index;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.index.AliasAction;
import org.springframework.data.elasticsearch.core.index.AliasActionParameters;
import org.springframework.data.elasticsearch.core.index.AliasActions;
import org.springframework.data.elasticsearch.core.index.AliasData;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * ES 索引别名管理器。
 * <p>
 * 支持为索引添加别名、移除别名、查询别名，以及零停机别名切换。
 * <pre>{@code
 * // 为索引添加别名
 * esAliasManager.addAlias("product_index", "product_alias");
 *
 * // 零停机切换（新索引承接流量）
 * esAliasManager.swapAlias("product_index_v2", "product_alias", "product_index_v1");
 * }</pre>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class EsAliasManager {

    private final ElasticsearchOperations elasticsearchOperations;

    /**
     * 为指定索引添加别名。
     *
     * @param index     索引名
     * @param aliasName 别名
     * @return true 操作成功
     */
    public boolean addAlias(String index, String aliasName) {
        try {
            AliasActionParameters params = AliasActionParameters.builder()
                .withIndices(index)
                .withAliases(aliasName)
                .build();
            AliasActions actions = new AliasActions(new AliasAction.Add(params));
            elasticsearchOperations.indexOps(IndexCoordinates.of(index)).alias(actions);
            log.info("Alias [{}] added to index [{}]", aliasName, index);
            return true;
        } catch (Exception e) {
            log.error("Failed to add alias [{}] to index [{}]", aliasName, index, e);
            return false;
        }
    }

    /**
     * 移除索引上的别名。
     *
     * @param index     索引名
     * @param aliasName 别名
     * @return true 操作成功
     */
    public boolean removeAlias(String index, String aliasName) {
        try {
            AliasActionParameters params = AliasActionParameters.builder()
                .withIndices(index)
                .withAliases(aliasName)
                .build();
            AliasActions actions = new AliasActions(new AliasAction.Remove(params));
            elasticsearchOperations.indexOps(IndexCoordinates.of(index)).alias(actions);
            log.info("Alias [{}] removed from index [{}]", aliasName, index);
            return true;
        } catch (Exception e) {
            log.error("Failed to remove alias [{}] from index [{}]", aliasName, index, e);
            return false;
        }
    }

    /**
     * 查询指定索引下的所有别名。
     *
     * @param index 索引名
     * @return 别名集合，不会返回 null
     */
    public Set<String> getAliases(String index) {
        var aliasMap = elasticsearchOperations.indexOps(IndexCoordinates.of(index)).getAliases();
        return aliasMap.getOrDefault(index, Set.of()).stream()
            .map(AliasData::getAlias)
            .collect(Collectors.toSet());
    }

    /**
     * 检查别名是否存在。
     *
     * @param aliasName 别名
     * @return true 别名存在
     */
    public boolean existsAlias(String aliasName) {
        return elasticsearchOperations.indexOps(IndexCoordinates.of(aliasName)).exists();
    }

    /**
     * 零停机别名切换。
     * <p>
     * 将别名从旧索引移除并添加到新索引，实现流量平滑迁移。
     * 建议配合索引滚动策略（按日期命名索引）使用。
     *
     * @param newIndex  新索引名
     * @param aliasName 别名
     * @param oldIndex  旧索引名
     * @return true 操作成功
     */
    public boolean swapAlias(String newIndex, String aliasName, String oldIndex) {
        try {
            AliasActionParameters removeParams = AliasActionParameters.builder()
                .withIndices(oldIndex)
                .withAliases(aliasName)
                .build();
            AliasActionParameters addParams = AliasActionParameters.builder()
                .withIndices(newIndex)
                .withAliases(aliasName)
                .build();
            AliasActions actions = new AliasActions(
                new AliasAction.Remove(removeParams),
                new AliasAction.Add(addParams)
            );
            elasticsearchOperations.indexOps(IndexCoordinates.of(newIndex)).alias(actions);
            log.info("Alias [{}] swapped from index [{}] to [{}]", aliasName, oldIndex, newIndex);
            return true;
        } catch (Exception e) {
            log.error("Failed to swap alias [{}] from [{}] to [{}]", aliasName, oldIndex, newIndex, e);
            return false;
        }
    }
}
