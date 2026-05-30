package com.sloth.boot.example.service.redis;

import com.sloth.boot.common.security.xss.XssCleaner;
import com.sloth.boot.example.domain.entity.DemoOrder;
import com.sloth.boot.example.domain.entity.Product;
import com.sloth.boot.example.domain.mapper.OrderMapper;
import com.sloth.boot.example.domain.mapper.ProductMapper;
import com.sloth.boot.example.model.order.dto.OrderDTO;
import com.sloth.boot.example.model.order.event.OrderStatusEvent;
import com.sloth.boot.example.model.order.request.OrderCreateRequest;
import com.sloth.boot.example.model.product.dto.ProductDTO;
import com.sloth.boot.example.model.product.request.ProductCreateRequest;
import com.sloth.boot.starter.redis.bloom.RedisBloomFilter;
import com.sloth.boot.starter.redis.core.RedisCacheUtil;
import com.sloth.boot.starter.redis.pubsub.RedisPubSubTemplate;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Redis 能力演示服务 - 合并商品、订单管理及全部 Redis 能力演示
 * <p>
 * 商品和订单均使用 DB 持久化（MyBatis-Plus），Redis 用于演示各类高级能力：
 * <ul>
 *   <li>布隆过滤器（RedisBloomFilter）— 防缓存穿透</li>
 *   <li>逻辑过期缓存（RedisCacheUtil.getWithLogicalExpire）— 防缓存击穿</li>
 *   <li>分布式锁（@DistributedLock）— 防并发超卖</li>
 *   <li>幂等注解（@Idempotent）— 防重复提交</li>
 *   <li>限流注解（@RateLimit）— 接口限流</li>
 *   <li>ZSet 排行榜（RedisCacheUtil.zAdd/zRange）— 投票排名</li>
 *   <li>Pub/Sub 事件（RedisPubSubTemplate）— 实时事件推送</li>
 *   <li>XSS 清洗（XssCleaner）— 防存储型 XSS</li>
 * </ul>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisDemoService {

    private static final int MAX_EVENTS = 100;

    private final RedisCacheUtil cacheUtil;
    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;

    /** 布隆过滤器（Redis 未启用时为 null） */
    @Nullable
    private final RedisBloomFilter<String> bloomFilter;

    /** Redis Pub/Sub 模板（未启用 Redis 时为 null） */
    @Nullable
    private final RedisPubSubTemplate pubSubTemplate;

    /** 接收到的事件列表（Pub/Sub 订阅回调写入） */
    private final CopyOnWriteArrayList<OrderStatusEvent> receivedEvents = new CopyOnWriteArrayList<>();

    @PostConstruct
    public void init() {
        // 初始化布隆过滤器：将 DB 中已有商品 ID 注册进去
        if (bloomFilter != null) {
            List<Product> products = productMapper.selectList(null);
            for (Product product : products) {
                bloomFilter.add(String.valueOf(product.getId()));
            }
            log.info("布隆过滤器初始化完成, 注册 {} 个商品ID", products.size());
        }

        // 初始化 Pub/Sub 订阅
        if (pubSubTemplate != null) {
            pubSubTemplate.subscribe("demo:order:events", OrderStatusEvent.class, event -> {
                log.info("收到订单事件: orderId={}, status={}", event.getOrderId(), event.getStatus());
                receivedEvents.add(event);
                while (receivedEvents.size() > MAX_EVENTS) {
                    receivedEvents.remove(0);
                }
            });
            log.info("订单事件订阅已初始化");
        }
    }

    // ==================== 商品相关 ====================

    /**
     * 查询商品 - 布隆过滤器穿透防护 + 逻辑过期缓存
     *
     * @param id 商品ID
     * @return 商品信息，不存在时返回 null
     */
    public ProductDTO getProduct(Long id) {
        // 布隆过滤器拦截不存在的 key，防止缓存穿透
        if (bloomFilter != null && !bloomFilter.mightContain(String.valueOf(id))) {
            log.debug("布隆过滤器拦截: id={}", id);
            return null;
        }

        return cacheUtil.getWithLogicalExpire("product:" + id, ProductDTO.class,
                () -> {
                    Product product = productMapper.selectById(id);
                    return product != null ? toProductDTO(product) : null;
                }, Duration.ofMinutes(30));
    }

    /**
     * 查询全部商品
     *
     * @return 商品列表
     */
    public List<ProductDTO> listProducts() {
        List<Product> products = productMapper.selectList(null);
        return products.stream().map(this::toProductDTO).toList();
    }

    /**
     * 创建商品 - XSS 清洗 + 布隆过滤器注册 + DB 持久化
     *
     * @param request 创建请求
     * @return 创建的商品
     */
    public ProductDTO createProduct(ProductCreateRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        // 对描述内容做 XSS 清洗，防止存储型 XSS
        product.setDescription(XssCleaner.cleanText(request.getDescription()));
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setStatus(0);
        productMapper.insert(product);

        // 注册到布隆过滤器
        if (bloomFilter != null) {
            bloomFilter.add(String.valueOf(product.getId()));
        }

        // 写入缓存
        cacheUtil.set("product:" + product.getId(), toProductDTO(product), Duration.ofMinutes(30));

        log.info("创建商品成功: id={}, name={}", product.getId(), product.getName());
        return toProductDTO(product);
    }

    /**
     * 删除商品 - 布隆过滤器清理 + 缓存清理 + DB 逻辑删除
     *
     * @param id 商品ID
     * @return 是否删除成功
     */
    public boolean deleteProduct(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            return false;
        }
        // 逻辑删除（BaseEntity.deleted 会被标记为 1）
        int rows = productMapper.deleteById(id);
        if (rows > 0) {
            cacheUtil.delete("product:" + id);
            log.info("删除商品成功: id={}", id);
            return true;
        }
        return false;
    }

    // ==================== 订单相关 ====================

    /**
     * 创建订单
     * <p>
     * 注意：分布式锁和幂等注解在 Controller 层配置
     *
     * @param request 创建请求
     * @param userId  用户ID
     * @return 创建的订单
     */
    public OrderDTO createOrder(OrderCreateRequest request, Long userId) {
        // 查找商品
        Product product = productMapper.selectById(request.getProductId());
        if (product == null) {
            throw new IllegalArgumentException("商品不存在: " + request.getProductId());
        }
        if (product.getStock() <= 0) {
            throw new IllegalArgumentException("商品库存不足: " + product.getName());
        }

        // 构建订单实体
        DemoOrder order = new DemoOrder();
        order.setUserId(userId);
        order.setProductId(product.getId());
        order.setProductName(product.getName());
        order.setQuantity(request.getQuantity());
        order.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
        order.setStatus("CREATED");
        orderMapper.insert(order);

        // 发布订单创建事件
        publishEvent(order.getId(), "CREATED", "订单已创建");

        log.info("创建订单成功: orderId={}, product={}, amount={}",
                order.getId(), product.getName(), order.getTotalPrice());
        return toOrderDTO(order);
    }

    /**
     * 查询全部订单（按创建时间倒序）
     *
     * @return 订单列表
     */
    public List<OrderDTO> listOrders() {
        return orderMapper.selectList(null).stream()
                .sorted(Comparator.comparing(DemoOrder::getCreateTime, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::toOrderDTO)
                .toList();
    }

    /**
     * 支付订单
     * <p>
     * 注意：分布式锁注解在 Controller 层配置
     *
     * @param id 订单ID
     * @return 更新后的订单
     */
    public OrderDTO payOrder(Long id) {
        DemoOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在: " + id);
        }
        order.setStatus("PAID");
        orderMapper.updateById(order);

        publishEvent(id, "PAID", "订单已支付");
        log.info("订单支付成功: orderId={}", id);
        return toOrderDTO(order);
    }

    /**
     * 获取最近的事件（从 Pub/Sub 订阅收到的）
     *
     * @param count 返回的事件数量
     * @return 最近的事件列表
     */
    public List<OrderStatusEvent> getRecentEvents(int count) {
        if (receivedEvents.isEmpty()) {
            return Collections.emptyList();
        }
        int size = receivedEvents.size();
        int fromIndex = Math.max(0, size - count);
        return new ArrayList<>(receivedEvents.subList(fromIndex, size));
    }

    // ==================== Redis 能力演示 ====================

    /**
     * 演示三种缓存策略的性能对比
     *
     * @return 各策略的执行结果与耗时
     */
    public Map<String, Object> demoCacheStrategies() {
        String key = "demo:key";
        String dbValue = "Hello SlothBoot";

        // 模拟慢查询的供应商
        java.util.function.Supplier<String> slowSupplier = () -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return dbValue;
        };

        Map<String, Object> result = new LinkedHashMap<>();

        // 策略1: 基础缓存 get + set
        long start1 = System.currentTimeMillis();
        String val1 = cacheUtil.get(key, String.class);
        if (val1 == null) {
            val1 = slowSupplier.get();
            cacheUtil.set(key, val1, Duration.ofMinutes(10));
        }
        long time1 = System.currentTimeMillis() - start1;
        result.put("basicCache", Map.of("value", val1, "timeMs", time1));

        // 策略2: getOrLoad（带空值保护）
        long start2 = System.currentTimeMillis();
        String val2 = cacheUtil.getOrLoad(key, String.class, slowSupplier, Duration.ofMinutes(10));
        long time2 = System.currentTimeMillis() - start2;
        result.put("getOrLoad", Map.of("value", val2, "timeMs", time2));

        // 策略3: 逻辑过期（异步重建）
        long start3 = System.currentTimeMillis();
        String val3 = cacheUtil.getWithLogicalExpire(key, String.class, slowSupplier, Duration.ofMinutes(10));
        long time3 = System.currentTimeMillis() - start3;
        result.put("logicalExpire", Map.of("value", val3, "timeMs", time3));

        return result;
    }

    /**
     * 获取布隆过滤器统计信息
     *
     * @return 布隆过滤器统计信息
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
     * 重置布隆过滤器，并重新注册 DB 中所有商品 ID
     */
    public void resetBloom() {
        if (bloomFilter != null) {
            bloomFilter.reset();
            List<Product> products = productMapper.selectList(null);
            for (Product product : products) {
                bloomFilter.add(String.valueOf(product.getId()));
            }
            log.info("布隆过滤器已重置, 重新注册 {} 个商品", products.size());
        }
    }

    /**
     * 获取商品排行榜（ZSet 排名 Top10）
     *
     * @return 排行榜数据
     */
    public Set<Object> getRank() {
        return cacheUtil.zRange("product:rank", 0, 9);
    }

    /**
     * 为商品投票（写入 ZSet，increment 实现累加）
     *
     * @param productId 商品ID
     */
    public void voteProduct(Long productId) {
        cacheUtil.zAdd("product:rank", String.valueOf(productId), Math.random() * 100);
        log.info("商品投票: productId={}", productId);
    }

    // ==================== 私有方法 ====================

    /**
     * 发布订单状态事件
     */
    private void publishEvent(Long orderId, String status, String message) {
        OrderStatusEvent event = new OrderStatusEvent(orderId, status, message, LocalDateTime.now());
        if (pubSubTemplate != null) {
            pubSubTemplate.publish("demo:order:events", event);
        }
        // 本地也记录一份，保证无 Redis 时演示可用
        receivedEvents.add(event);
        while (receivedEvents.size() > MAX_EVENTS) {
            receivedEvents.removeFirst();
        }
    }

    /**
     * Product 实体 -> ProductDTO
     */
    private ProductDTO toProductDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setCreateTime(product.getCreateTime());
        return dto;
    }

    /**
     * DemoOrder 实体 -> OrderDTO
     */
    private OrderDTO toOrderDTO(DemoOrder order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setProductId(order.getProductId());
        dto.setProductName(order.getProductName());
        dto.setAmount(order.getTotalPrice());
        dto.setQuantity(order.getQuantity());
        dto.setStatus(order.getStatus());
        dto.setCreateTime(order.getCreateTime());
        dto.setUpdateTime(order.getUpdateTime());
        return dto;
    }
}
