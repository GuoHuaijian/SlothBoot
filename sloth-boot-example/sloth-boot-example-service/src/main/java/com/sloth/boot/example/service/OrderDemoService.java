package com.sloth.boot.example.service;

import com.sloth.boot.example.dto.OrderCreateRequest;
import com.sloth.boot.example.dto.OrderDTO;
import com.sloth.boot.example.dto.OrderStatusEvent;
import com.sloth.boot.example.dto.ProductDTO;
import com.sloth.boot.starter.redis.core.RedisCacheUtil;
import com.sloth.boot.starter.redis.pubsub.RedisPubSubTemplate;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 订单演示服务 - 展示分布式锁、幂等、限流、Redis Pub/Sub 事件发布等能力
 */
@Slf4j
@Service
public class OrderDemoService {

    private static final int MAX_EVENTS = 100;

    private final RedisCacheUtil cacheUtil;
    private final ProductDemoService productService;

    /** Redis Pub/Sub 模板（未启用 Redis 时为 null） */
    private final RedisPubSubTemplate pubSubTemplate;

    /** 内存订单存储 */
    private final ConcurrentHashMap<Long, OrderDTO> orders = new ConcurrentHashMap<>();

    /** 接收到的事件列表（Pub/Sub 订阅回调写入） */
    private final CopyOnWriteArrayList<OrderStatusEvent> receivedEvents = new CopyOnWriteArrayList<>();

    public OrderDemoService(RedisCacheUtil cacheUtil,
                            ProductDemoService productService,
                            @Autowired(required = false) RedisPubSubTemplate pubSubTemplate) {
        this.cacheUtil = cacheUtil;
        this.productService = productService;
        this.pubSubTemplate = pubSubTemplate;
    }

    @PostConstruct
    public void init() {
        if (pubSubTemplate != null) {
            pubSubTemplate.subscribe("demo:order:events", OrderStatusEvent.class, event -> {
                log.info("收到订单事件: orderId={}, status={}", event.getOrderId(), event.getStatus());
                receivedEvents.add(event);
                // 超过上限时移除最早的事件
                while (receivedEvents.size() > MAX_EVENTS) {
                    receivedEvents.remove(0);
                }
            });
            log.info("订单事件订阅已初始化");
        }
    }

    /**
     * 创建订单
     */
    public OrderDTO createOrder(OrderCreateRequest request, Long userId) {
        // 查找商品
        ProductDTO product = productService.getProduct(request.getProductId());
        if (product == null) {
            throw new IllegalArgumentException("商品不存在: " + request.getProductId());
        }
        if (product.getStock() <= 0) {
            throw new IllegalArgumentException("商品库存不足: " + product.getName());
        }

        // 使用 Redis 自增生成订单 ID
        Long orderId = cacheUtil.increment("demo:order:id", 1);

        OrderDTO order = new OrderDTO();
        order.setId(orderId);
        order.setUserId(userId);
        order.setProductId(product.getId());
        order.setProductName(product.getName());
        order.setAmount(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
        order.setQuantity(request.getQuantity());
        order.setStatus("CREATED");
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        orders.put(orderId, order);

        // 发布订单创建事件
        publishEvent(orderId, "CREATED", "订单已创建");

        log.info("创建订单成功: orderId={}, product={}, amount={}", orderId, product.getName(), order.getAmount());
        return order;
    }

    /**
     * 查询订单
     */
    public OrderDTO getOrder(Long id) {
        return orders.get(id);
    }

    /**
     * 查询全部订单（按创建时间倒序）
     */
    public List<OrderDTO> listOrders() {
        return orders.values().stream()
                .sorted(Comparator.comparing(OrderDTO::getCreateTime).reversed())
                .toList();
    }

    /**
     * 支付订单
     */
    public OrderDTO payOrder(Long id) {
        OrderDTO order = orders.get(id);
        if (order == null) {
            return null;
        }
        order.setStatus("PAID");
        order.setUpdateTime(LocalDateTime.now());
        orders.put(id, order);

        publishEvent(id, "PAID", "订单已支付");
        log.info("订单支付成功: orderId={}", id);
        return order;
    }

    /**
     * 取消订单
     */
    public OrderDTO cancelOrder(Long id) {
        OrderDTO order = orders.get(id);
        if (order == null) {
            return null;
        }
        order.setStatus("CANCELLED");
        order.setUpdateTime(LocalDateTime.now());
        orders.put(id, order);

        publishEvent(id, "CANCELLED", "订单已取消");
        log.info("订单已取消: orderId={}", id);
        return order;
    }

    /**
     * 获取最近的事件（从 Pub/Sub 订阅收到的）
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
            receivedEvents.remove(0);
        }
    }
}
