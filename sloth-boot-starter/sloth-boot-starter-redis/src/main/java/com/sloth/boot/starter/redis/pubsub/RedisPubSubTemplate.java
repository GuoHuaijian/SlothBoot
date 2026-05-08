package com.sloth.boot.starter.redis.pubsub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Redis Pub/Sub 消息模板。
 * <p>
 * 提供类型化的消息发布和订阅能力，消息以 JSON 格式序列化/反序列化。
 * <p>
 * 使用示例：
 *
 * <pre>
 * // 发布消息
 * pubSubTemplate.publish("order:created", new OrderEvent("123", "iPhone"));
 *
 * // 订阅消息
 * pubSubTemplate.subscribe("order:created", OrderEvent.class, event -&gt; {
 *     log.info("收到订单事件: {}", event.getOrderId());
 * });
 * </pre>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
public class RedisPubSubTemplate {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final RedisMessageListenerContainer listenerContainer;
    private final MessageListenerAdapter messageListenerAdapter;
    private final AtomicBoolean containerStarted = new AtomicBoolean(false);

    /**
     * 创建 Redis Pub/Sub 模板。
     *
     * @param stringRedisTemplate StringRedisTemplate
     * @param objectMapper        JSON 序列化器
     * @param connectionFactory   Redis 连接工厂
     */
    public RedisPubSubTemplate(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper,
                               RedisConnectionFactory connectionFactory) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
        this.messageListenerAdapter = new MessageListenerAdapter(objectMapper);

        this.listenerContainer = new RedisMessageListenerContainer();
        this.listenerContainer.setConnectionFactory(connectionFactory);
    }

    /**
     * 发布消息到指定频道。
     *
     * @param channel 频道名称
     * @param message 消息对象（将序列化为 JSON）
     */
    public void publish(String channel, Object message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            stringRedisTemplate.convertAndSend(channel, json);
            log.debug("消息已发布, channel={}", channel);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("消息序列化失败, channel=" + channel, ex);
        }
    }

    /**
     * 订阅频道并注册类型化消息监听器。
     *
     * @param channel  频道名称
     * @param type     消息类型
     * @param listener 消息处理器
     * @param <T>      消息类型
     */
    public <T> void subscribe(String channel, Class<T> type, Consumer<T> listener) {
        messageListenerAdapter.addListener(channel, type, listener);

        Topic topic = new ChannelTopic(channel);
        listenerContainer.addMessageListener(messageListenerAdapter, topic);

        if (containerStarted.compareAndSet(false, true)) {
            listenerContainer.afterPropertiesSet();
            listenerContainer.start();
            log.info("Redis 消息监听容器已启动");
        }

        log.info("已订阅频道: {}", channel);
    }
}
