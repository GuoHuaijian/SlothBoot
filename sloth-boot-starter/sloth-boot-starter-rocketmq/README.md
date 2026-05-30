# Sloth Boot Starter RocketMQ

消息队列增强组件，基于 RocketMQ 提供 TraceId 自动透传、消费幂等、重试监控及多种消息发送模式。

## Maven 依赖

```xml
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-starter-rocketmq</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## 配置项

| 配置项 | 类型 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `sloth.mq.enabled` | `boolean` | `true` | 是否启用 MQ Starter |
| `sloth.mq.idempotent-enabled` | `boolean` | `true` | 是否启用消费幂等（基于 Redis） |
| `sloth.mq.max-retry` | `int` | `3` | 最大重试次数 |
| `sloth.mq.consume-idempotent-key-prefix` | `String` | `sloth:mq:consume:` | 消费幂等 Redis Key 前缀 |
| `sloth.mq.transaction-producer-group` | `String` | `sloth-tx-producer-group` | 事务消息生产者组 |

## 核心组件

| 组件 | 说明 |
| --- | --- |
| `MessageProducer` | 消息生产者，支持同步、异步、单向、延迟、顺序、事务发送 |
| `AbstractMessageListener<T>` | 消费者基类，内置重试计数、幂等校验、TraceId 恢复 |
| `BaseMessage` | 消息基类，自动填充 msgId、traceId、createTime |
| `TraceMessageInterceptor` | TraceId 消息属性注入与恢复 |
| `RocketMQHealthIndicator` | RocketMQ 健康检查 |

## 生产者示例

```java
@Service
@RequiredArgsConstructor
public class OrderEventProducer {

    private final MessageProducer messageProducer;

    public void sendOrderCreated(Long orderId) {
        OrderMessage msg = new OrderMessage();
        msg.setBizKey(String.valueOf(orderId));
        msg.setOrderId(orderId);
        messageProducer.sendSync("order-topic", "created", msg);
    }

    public void sendDelayCancel(Long orderId, int delayLevel) {
        OrderMessage msg = new OrderMessage();
        msg.setBizKey(String.valueOf(orderId));
        messageProducer.sendDelay("order-topic", msg, delayLevel);
    }
}
```

## 消费者示例

```java
@Component
@RocketMQMessageListener(topic = "order-topic", selectorExpression = "created",
        consumerGroup = "order-created-consumer")
public class OrderCreatedListener extends AbstractMessageListener<OrderMessage> {

    public OrderCreatedListener(RocketMQProperties rocketMQProperties,
                                ObjectProvider<StringRedisTemplate> redisTemplate) {
        super(rocketMQProperties, redisTemplate);
    }

    @Override
    protected void onMessage(OrderMessage message) {
        log.info("收到订单创建消息, orderId={}", message.getOrderId());
        orderService.handleOrderCreated(message.getOrderId());
    }
}
```

## 自定义消息体

```java
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderMessage extends BaseMessage {
    private Long orderId;
    private String orderNo;
}
```

## FAQ

**Q: 消费幂等如何实现？**
A: 基于 Redis `setIfAbsent`，以 `sloth:mq:consume:{msgId}` 为 Key，24 小时过期。重复消费时直接跳过。

**Q: 如何关闭消费幂等？**
A: 设置 `sloth.mq.idempotent-enabled=false`。

**Q: 消息重试超过最大次数后怎么办？**
A: 超过 `max-retry` 后会记录错误日志，消息进入 RocketMQ 死信队列，需人工介入处理。
