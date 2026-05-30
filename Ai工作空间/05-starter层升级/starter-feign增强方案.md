# starter-feign 增强方案

> 优先级: P2 | 当前: 11 文件 | 目标: 22 文件

---

## 一、当前问题

1. **无 Micrometer 指标** — Feign 调用的次数、延迟、错误率无法监控
2. **无 OkHttp 连接池指标** — 连接池状态不可观测
3. **无重试机制** — OkHttp 层面有 retry，但 Feign 层面无可控重试策略
4. **无健康检查** — 无法检查下游 Feign 服务是否可达
5. **无请求体日志** — `FeignLogConfig` 只设日志级别，不记录请求体
6. **熔断集成不完整** — `sentinelEnabled` 配置存在但未连接 Sentinel 代码
7. **测试不完整** — 只有 AutoConfiguration 测试

---

## 二、增强方案

### 2.1 Metrics（P1）

```
metrics/FeignMetricsInterceptor.java
- 包装 Feign Client
- 计数器: feign.call.total (tags: service, method)
- 计数器: feign.call.error (tags: service, method, httpStatus)
- 计时器: feign.call.duration (tags: service, method)
- 条件: @ConditionalOnClass(MeterRegistry.class)

metrics/OkHttpConnectionPoolMetrics.java
- Gauge: feign.okhttp.connections.idle
- Gauge: feign.okhttp.connections.active
- 从 ConnectionPool 读取
```

### 2.2 HealthIndicator（P2）

```
health/FeignHealthIndicator.java
- 扫描所有 @FeignClient bean
- 对每个服务尝试轻量检查（HEAD / 或 /actuator/health）
- 报告: registeredClients 数量, 每个服务的 up/down 状态
- 超时: 3 秒（不阻塞健康检查）
- 条件: @ConditionalOnClass(FeignClient.class)
```

### 2.3 重试机制（P2）

```
retry/FeignRetryer.java
- 实现 feign.Retryer
- 配置: sloth.feign.retry.max-attempts=3, backoff-ms=100, retryable-statuses=502,503,504
- 仅对 5xx 和网络异常重试
- 注册: @ConditionalOnMissingBean(Retryer.class)
```

### 2.4 请求体日志（P2）

```
logging/FeignBodyLoggingFilter.java
- 包装 Feign Client
- DEBUG 级别记录: URL, headers(脱敏), request body(截断), response status, response body(截断)
- 配置: sloth.feign.logging.body-enabled=false, max-body-length=2048
- 条件: @ConditionalOnProperty(prefix = "sloth.feign.logging", name = "body-enabled")
```

### 2.5 熔断集成（P2）

```
config/SentinelFeignAutoConfiguration.java
- 条件: @ConditionalOnProperty(sloth.feign.sentinel-enabled=true) + @ConditionalOnClass(SphU.class)
- 自动设置 feign.sentinel.enabled=true
- 注册 Sentinel 相关 Feign 配置
```

### 2.6 测试补充（P3）

```
decoder/FeignResponseDecoderTest.java
- 测试: R<T> 正常解包
- 测试: R<T> code!=0 时抛异常
- 测试: 非 R<T> 响应直接解码

decoder/FeignErrorDecoderTest.java
- 测试: 404 → RemoteServiceNotFoundException
- 测试: 429 → RemoteRateLimitException
- 测试: 500 → RemoteCallException

config/FeignRequestInterceptorTest.java
- 测试: traceId 传播
- 测试: token 传播
- 测试: 用户上下文传播
```

---

## 三、新增文件清单

| 文件 | 类型 | 说明 |
|------|------|------|
| `metrics/FeignMetricsInterceptor.java` | 新增 | 调用指标 |
| `metrics/OkHttpConnectionPoolMetrics.java` | 新增 | 连接池指标 |
| `health/FeignHealthIndicator.java` | 新增 | 健康检查 |
| `retry/FeignRetryer.java` | 新增 | 重试策略 |
| `logging/FeignBodyLoggingFilter.java` | 新增 | 请求体日志 |
| `config/SentinelFeignAutoConfiguration.java` | 新增 | Sentinel 熔断集成 |
| `decoder/FeignResponseDecoderTest.java` | 新增 | 解码器测试 |
| `decoder/FeignErrorDecoderTest.java` | 新增 | 错误解码器测试 |
| `config/FeignRequestInterceptorTest.java` | 新增 | 拦截器测试 |
| `config/FeignAutoConfiguration.java` | 修改 | 注册新 bean |
| `config/FeignProperties.java` | 修改 | 增加重试/日志配置 |
