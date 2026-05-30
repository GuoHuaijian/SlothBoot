# starter-job 增强方案

> 优先级: P1 | 当前: 5 文件 | 目标: 15 文件

---

## 一、当前问题

1. **无重试机制** — Job 失败后没有框架级重试支持
2. **无执行指标** — 不知道 Job 的执行次数、成功率、耗时
3. **无事件发布** — Job 执行结果无法被其他模块感知（如告警）
4. **无拦截器 SPI** — 无法为所有 Job 统一添加前置/后置逻辑
5. **无 Actuator Endpoint** — 运行时无法查看 Job 状态
6. **上下文传播不完整** — 只传播 TraceContext，不传播 UserContext

---

## 二、增强方案

### 2.1 事件发布（P1）

```
event/JobExecutionEvent.java
- 继承 BaseEvent
- 字段: handlerName, traceId, startTime, endTime, costTime, success, errorMessage
- 发布位置: AbstractJobHandler.execute() 的 finally 块中
```

### 2.2 执行拦截器 SPI（P1）

```
spi/JobInterceptor.java
- 接口:
    void beforeExecute(String handlerName)
    void afterExecute(String handlerName, boolean success, long costMs, Throwable error)

spi/JobInterceptorChain.java
- 收集所有 JobInterceptor bean（@Order 排序）
- 在 AbstractJobHandler.execute() 前后调用链
```

**AbstractJobHandler 修改**：
```java
// 当前: 直接调用 doExecute()
// 修改: 先调 interceptorChain.beforeExecute() → doExecute() → interceptorChain.afterExecute()
```

### 2.3 Metrics（P1）

```
metrics/JobMetrics.java
- 注入 MeterRegistry
- 计数器: job.execution.total (tags: handlerName)
- 计数器: job.execution.success (tags: handlerName)
- 计数器: job.execution.failure (tags: handlerName, errorType)
- 计时器: job.execution.duration (tags: handlerName)
- 集成位置: AbstractJobHandler.execute() 或 JobInterceptor 实现
```

### 2.4 HealthIndicator（P1）

```
health/JobHealthIndicator.java
- 检查: XxlJobSpringExecutor bean 是否存在
- 检查: adminAddresses 是否可达（轻量 HTTP HEAD）
- 报告: adminAddresses, appname, address, port
```

### 2.5 重试支持（P2）

```
annotation/RetryableJob.java
- @Target(METHOD), @Retention(RUNTIME)
- 属性: maxAttempts=3, backoffDelay=1000ms, backoffMultiplier=2.0

core/RetryableJobHandler.java
- 装饰器，包装任意 AbstractJobHandler
- 读取 @RetryableJob 配置
- 重试间隔: backDelay * (backoffMultiplier ^ attempt)
- 仅对异常重试，正常返回不重试
```

### 2.6 Actuator Endpoint（P2）

```
endpoint/JobEndpoint.java
- @Endpoint(id = "xxlJob")
- 操作:
    listHandlers(): 返回已注册 handler 名称列表
    handlerStats(handlerName): 返回最近 N 次执行统计（内存环形缓冲区）
- 条件: @ConditionalOnAvailableEndpoint
```

### 2.7 上下文传播（P2）

```
core/JobContextPropagator.java
- 在 AbstractJobHandler.execute() 入口捕获: TraceContext + UserContext
- 已有 TraceContext 处理，需增加 UserContext 处理
- 使用 ContextSnapshot 捕获/恢复
```

---

## 三、新增文件清单

| 文件 | 类型 | 说明 |
|------|------|------|
| `event/JobExecutionEvent.java` | 新增 | Job 执行事件 |
| `spi/JobInterceptor.java` | 新增 | 执行拦截器接口 |
| `spi/JobInterceptorChain.java` | 新增 | 拦截器链管理 |
| `metrics/JobMetrics.java` | 新增 | Micrometer 指标 |
| `health/JobHealthIndicator.java` | 新增 | 健康检查 |
| `annotation/RetryableJob.java` | 新增 | 重试注解 |
| `core/RetryableJobHandler.java` | 新增 | 重试装饰器 |
| `endpoint/JobEndpoint.java` | 新增 | Actuator Endpoint |
| `core/JobContextPropagator.java` | 新增 | 上下文传播增强 |
| `core/AbstractJobHandler.java` | 修改 | 集成事件/拦截器/Metrics |
| `config/JobAutoConfiguration.java` | 修改 | 注册新 bean |
