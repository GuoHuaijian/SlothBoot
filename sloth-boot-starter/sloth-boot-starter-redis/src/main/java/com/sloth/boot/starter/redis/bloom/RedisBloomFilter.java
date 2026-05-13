package com.sloth.boot.starter.redis.bloom;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;

/**
 * Redis 布隆过滤器工具类。
 * <p>
 * 基于 Redisson 的 RBloomFilter 实现，提供高效的元素存在性判断， 适用于缓存穿透防护、去重等场景。
 * <p>
 * 注意：布隆过滤器存在误判（false positive），即不存在的元素可能被判断为存在， 但已存在的元素一定会被正确识别。
 *
 * @param <T> 元素类型
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
public class RedisBloomFilter<T> {

    private final RBloomFilter<T> bloomFilter;
    private final long expectedInsertions;
    private final double falsePositiveProbability;

    /**
     * 创建布隆过滤器。
     *
     * @param redissonClient           Redisson 客户端
     * @param name                     布隆过滤器名称（对应 Redis key）
     * @param expectedInsertions       预期插入元素数量
     * @param falsePositiveProbability 误判概率
     */
    public RedisBloomFilter(RedissonClient redissonClient, String name, long expectedInsertions,
                            double falsePositiveProbability) {
        this.expectedInsertions = expectedInsertions;
        this.falsePositiveProbability = falsePositiveProbability;
        this.bloomFilter = redissonClient.getBloomFilter(name);
        this.bloomFilter.tryInit(expectedInsertions, falsePositiveProbability);
        log.info("Redis 布隆过滤器已初始化, name={}, expectedInsertions={}, falsePositiveProbability={}", name,
            expectedInsertions, falsePositiveProbability);
    }

    /**
     * 向布隆过滤器添加元素。
     *
     * @param element 待添加元素
     * @return 如果元素可能已存在返回 false，如果元素肯定不存在并已添加返回 true
     */
    public boolean add(T element) {
        return bloomFilter.add(element);
    }

    /**
     * 判断元素是否可能存在。
     *
     * @param element 待判断元素
     * @return true 表示元素可能存在（有误判概率），false 表示元素肯定不存在
     */
    public boolean mightContain(T element) {
        return bloomFilter.contains(element);
    }

    /**
     * 获取预期插入元素数量。
     *
     * @return 预期插入元素数量
     */
    public long getExpectedInsertions() {
        return expectedInsertions;
    }

    /**
     * 获取误判概率。
     *
     * @return 误判概率
     */
    public double getFalsePositiveProbability() {
        return falsePositiveProbability;
    }

    /**
     * 获取布隆过滤器中已添加的元素近似数量。
     *
     * @return 已添加元素近似数量
     */
    public long count() {
        return bloomFilter.count();
    }

    /**
     * 重置布隆过滤器，清除所有已添加的元素。
     * <p>
     * 注意：该操作不可逆，会清空所有数据。
     */
    public void reset() {
        bloomFilter.delete();
        bloomFilter.tryInit(expectedInsertions, falsePositiveProbability);
        log.info("Redis 布隆过滤器已重置");
    }
}
