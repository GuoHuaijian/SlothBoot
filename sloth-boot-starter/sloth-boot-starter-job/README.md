# Sloth Boot Starter Job

分布式任务调度组件，封装 XXL-Job 自动装配，提供 TraceId 自动注入、MDC 日志追踪及执行耗时统计。

## Maven 依赖

```xml
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-starter-job</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## 配置项

| 配置项 | 类型 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `sloth.job.admin-addresses` | `String` | - | XXL-Job Admin 地址 |
| `sloth.job.access-token` | `String` | - | 访问令牌 |
| `sloth.job.appname` | `String` | `spring.application.name` | 执行器应用名 |
| `sloth.job.address` | `String` | - | 执行器注册地址（为空则自动获取） |
| `sloth.job.ip` | `String` | - | 执行器 IP |
| `sloth.job.port` | `int` | `9999` | 执行器端口 |
| `sloth.job.log-path` | `String` | `./logs/xxl-job` | 日志路径 |
| `sloth.job.log-retention-days` | `int` | `30` | 日志保留天数 |

## 核心组件

| 组件 | 说明 |
| --- | --- |
| `AbstractJobHandler` | 作业处理器基类，自动注入 TraceId 到 MDC，记录执行耗时与异常 |
| `JobProperties` | XXL-Job 配置属性 |
| `JobAutoConfiguration` | 自动注册 `XxlJobSpringExecutor` |

## 配置示例

```yaml
sloth:
  job:
    admin-addresses: http://localhost:8080/xxl-job-admin
    access-token: default-token
    appname: sloth-order-service
    port: 9999
    log-path: ./logs/xxl-job
    log-retention-days: 30
```

## 作业处理器示例

```java
@Component
@XxlJob("orderTimeoutHandler")
public class OrderTimeoutHandler extends AbstractJobHandler {

    @Override
    protected void doExecute() throws Exception {
        // TraceId 已自动注入 MDC，日志中可直接输出 traceId
        List<Long> timeoutOrderIds = orderService.findTimeoutOrders();
        for (Long orderId : timeoutOrderIds) {
            orderService.cancelOrder(orderId);
        }
        log.info("超时订单处理完成, count={}", timeoutOrderIds.size());
    }
}
```

## 日志输出示例

```
INFO  - XXL-Job 开始执行, handler=OrderTimeoutHandler, traceId=abc123
INFO  - 超时订单处理完成, count=5
INFO  - XXL-Job 执行成功, handler=OrderTimeoutHandler, traceId=abc123, cost=1200ms
```

## FAQ

**Q: `appname` 不配置会怎样？**
A: 自动读取 `spring.application.name`，如果也未配置则默认为 `application`。

**Q: 如何在任务中获取 TraceId？**
A: `AbstractJobHandler` 已自动将 TraceId 注入 `MDC` 和 `TraceContext`，日志中配置 `%X{traceId}` 即可输出，代码中通过 `TraceContext.getTraceId()` 获取。

**Q: 如何注册多个执行器？**
A: 每个微服务独立引入依赖并配置不同的 `appname`，各自注册为独立执行器。
