package com.sloth.boot.example.application.command.redis;

import com.sloth.boot.example.application.model.convert.product.ProductConvert;
import com.sloth.boot.example.application.model.event.order.OrderStatusEvent;
import com.sloth.boot.example.application.model.vo.product.ProductVO;
import com.sloth.boot.example.infrastructure.model.po.product.Product;
import com.sloth.boot.example.infrastructure.repository.mapper.product.ProductMapper;
import com.sloth.boot.starter.redis.bloom.RedisBloomFilter;
import com.sloth.boot.starter.redis.core.RedisCacheStrategy;
import com.sloth.boot.starter.redis.core.RedisCacheUtil;
import com.sloth.boot.starter.redis.pubsub.RedisPubSubTemplate;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Redis 能力演示服务。
 * <p>
 * 演示布隆过滤器、逻辑过期缓存、ZSet 排行榜、Pub/Sub 事件等 Redis 高级能力。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisDemoCommand {

    private static final int MAX_EVENTS = 100;

    private final RedisCacheUtil cacheUtil;
    private final RedisCacheStrategy cacheStrategy;
    private final ProductMapper productMapper;
    private final ProductConvert productConvert;

    @Nullable
    private final RedisBloomFilter<String> bloomFilter;

    @Nullable
    private final RedisPubSubTemplate pubSubTemplate;

    private final CopyOnWriteArrayList<OrderStatusEvent> receivedEvents = new CopyOnWriteArrayList<>();

    @PostConstruct
    public void init() {
        if (bloomFilter != null) {
            List<Product> products = productMapper.listProduct();
            for (Product product : products) {
                bloomFilter.add(String.valueOf(product.getId()));
            }
            log.info("布隆过滤器初始化完成, 注册 {} 个商品ID", products.size());
        }

        if (pubSubTemplate != null) {
            pubSubTemplate.subscribe("demo:order:events", OrderStatusEvent.class, event -> {
                log.info("收到订单事件: orderId={}, status={}", event.getOrderId(), event.getStatus());
                receivedEvents.add(event);
                while (receivedEvents.size() > MAX_EVENTS) {
                    receivedEvents.removeFirst();
                }
            });
            log.info("订单事件订阅已初始化");
        }
    }

    /**
     * 查询商品 - 布隆过滤器穿透防护 + 逻辑过期缓存
     *
     */
    public ProductVO getProduct(Long id) {
        if (bloomFilter != null && !bloomFilter.mightContain(String.valueOf(id))) {
            log.debug("布隆过滤器拦截: id={}", id);
            return null;
        }
        Product product = cacheStrategy.getWithLogicalExpire("product:" + id, Product.class,
            () -> productMapper.selectById(id), Duration.ofMinutes(30));
        return product != null ? productConvert.toVO(product) : null;
    }

    /**
     * 演示三种缓存策略的性能对比
     *
     */
    public Map<String, Object> demoCacheStrategies() {
        String key = "demo:key";
        String dbValue = "Hello SlothBoot";

        java.util.function.Supplier<String> slowSupplier = () -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return dbValue;
        };

        Map<String, Object> result = new LinkedHashMap<>();

        long start1 = System.currentTimeMillis();
        String val1 = cacheUtil.get(key, String.class);
        if (val1 == null) {
            val1 = slowSupplier.get();
            cacheUtil.set(key, val1, Duration.ofMinutes(10));
        }
        result.put("basicCache", Map.of("value", val1, "timeMs", System.currentTimeMillis() - start1));

        long start2 = System.currentTimeMillis();
        String val2 = cacheStrategy.getOrLoad(key, String.class, slowSupplier, Duration.ofMinutes(10));
        result.put("getOrLoad", Map.of("value", val2, "timeMs", System.currentTimeMillis() - start2));

        long start3 = System.currentTimeMillis();
        String val3 = cacheStrategy.getWithLogicalExpire(key, String.class, slowSupplier, Duration.ofMinutes(10));
        result.put("logicalExpire", Map.of("value", val3, "timeMs", System.currentTimeMillis() - start3));

        return result;
    }

    /**
     * 获取布隆过滤器统计信息
     *
     */
    public Map<String, Object> getBloomStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        if (bloomFilter != null) {
            stats.put("count", bloomFilter.count());
            stats.put("expectedInsertions", bloomFilter.getExpectedInsertions());
            stats.put("falsePositiveProbability", bloomFilter.getFalsePositiveProbability());
        } else {
            stats.put("available", false);
        }
        return stats;
    }

    /**
     * 重置布隆过滤器
     *
     */
    public void resetBloom() {
        if (bloomFilter != null) {
            bloomFilter.reset();
            List<Product> products = productMapper.listProduct();
            for (Product product : products) {
                bloomFilter.add(String.valueOf(product.getId()));
            }
            log.info("布隆过滤器已重置, 重新注册 {} 个商品", products.size());
        }
    }

    /**
     * 获取商品排行榜（ZSet Top10）
     *
     */
    public Set<Object> getRank() {
        return cacheUtil.zRange("product:rank", 0, 9);
    }

    /**
     * 为商品投票
     *
     */
    public void voteProduct(Long productId) {
        cacheUtil.zAdd("product:rank", String.valueOf(productId), Math.random() * 100);
        log.info("商品投票: productId={}", productId);
    }

    /**
     * 获取最近的 Pub/Sub 事件
     *
     */
    public List<OrderStatusEvent> getRecentEvents(int count) {
        if (receivedEvents.isEmpty()) {
            return Collections.emptyList();
        }
        int size = receivedEvents.size();
        int fromIndex = Math.max(0, size - count);
        return new ArrayList<>(receivedEvents.subList(fromIndex, size));
    }

    /**
     * 发布订单状态事件（供 OrderCommand 调用）
     *
     */
    public void publishOrderEvent(Long orderId, String status, String message) {
        OrderStatusEvent event = OrderStatusEvent.of(this, orderId, status, message);
        if (pubSubTemplate != null) {
            pubSubTemplate.publish("demo:order:events", event);
        }
        receivedEvents.add(event);
        while (receivedEvents.size() > MAX_EVENTS) {
            receivedEvents.removeFirst();
        }
    }
}
