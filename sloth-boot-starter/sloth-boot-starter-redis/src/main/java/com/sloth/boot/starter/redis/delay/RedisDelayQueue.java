package com.sloth.boot.starter.redis.delay;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Redis 延迟队列工具类。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class RedisDelayQueue {

    private final RedissonClient redissonClient;
    private final Map<String, ExecutorService> consumers = new ConcurrentHashMap<>();

    /**
     * 投递延迟消息。
     *
     * @param queueName 队列名
     * @param message   消息体
     * @param delay     延迟时间
     * @param timeUnit  时间单位
     * @param <T>       消息类型
     */
    public <T> void offer(String queueName, T message, long delay, TimeUnit timeUnit) {
        RBlockingQueue<T> blockingQueue = redissonClient.getBlockingQueue(queueName);
        RDelayedQueue<T> delayedQueue = redissonClient.getDelayedQueue(blockingQueue);
        delayedQueue.offer(message, delay, timeUnit);
    }

    /**
     * 注册延迟消息监听器。
     *
     * @param queueName 队列名
     * @param handler   消费处理器
     * @param <T>       消息类型
     */
    public <T> void subscribe(String queueName, DelayedMessageHandler<T> handler) {
        consumers.computeIfAbsent(queueName, key -> {
            ExecutorService executorService = Executors.newSingleThreadExecutor(runnable -> {
                Thread thread = new Thread(runnable);
                thread.setName("sloth-redis-delay-" + key);
                thread.setDaemon(true);
                return thread;
            });
            executorService.submit(() -> consume(queueName, handler));
            return executorService;
        });
    }

    private <T> void consume(String queueName, DelayedMessageHandler<T> handler) {
        RBlockingQueue<T> queue = redissonClient.getBlockingQueue(queueName);
        while (!Thread.currentThread().isInterrupted()) {
            try {
                T message = queue.take();
                handler.handle(message);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                log.warn("延迟队列消费者已中断, queueName={}", queueName);
            } catch (Exception ex) {
                log.error("处理延迟消息失败, queueName={}", queueName, ex);
            }
        }
    }
}
