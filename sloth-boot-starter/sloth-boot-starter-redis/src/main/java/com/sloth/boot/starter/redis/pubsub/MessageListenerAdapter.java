package com.sloth.boot.starter.redis.pubsub;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Redis 消息监听适配器。
 * <p>
 * 实现 Spring Data Redis 的 MessageListener 接口， 根据频道（channel）路由到对应的类型化监听器，支持 JSON
 * 反序列化。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
public class MessageListenerAdapter implements MessageListener {

    private final ObjectMapper objectMapper;
    private final Map<String, TypedConsumer<?>> listeners = new ConcurrentHashMap<>();

    public MessageListenerAdapter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel(), StandardCharsets.UTF_8);
        String body = new String(message.getBody(), StandardCharsets.UTF_8);

        TypedConsumer consumer = listeners.get(channel);
        if (consumer == null) {
            log.warn("未找到频道监听器, channel={}", channel);
            return;
        }

        try {
            JavaType javaType = objectMapper.constructType(consumer.type);
            Object deserialized = objectMapper.readValue(body, javaType);
            consumer.accept(deserialized);
        } catch (Exception ex) {
            log.error("消息反序列化失败, channel={}, body={}", channel, body, ex);
        }
    }

    /**
     * 注册类型化监听器。
     *
     * @param channel  频道名称
     * @param type     消息类型
     * @param listener 消息处理器
     * @param <T>      消息类型
     */
    public <T> void addListener(String channel, Class<T> type, Consumer<T> listener) {
        listeners.put(channel, new TypedConsumer<>(type, listener));
    }

    /**
     * 获取已注册的频道集合。
     *
     * @return 频道集合
     */
    public java.util.Set<String> getChannels() {
        return listeners.keySet();
    }

    /**
     * 类型化消息消费者。
     *
     * @param <T> 消息类型
     */
    private record TypedConsumer<T>(Class<T> type, Consumer<T> consumer) {

        @SuppressWarnings("unchecked")
        void accept(Object message) {
            consumer.accept((T) message);
        }
    }
}
