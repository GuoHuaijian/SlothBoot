# Starter 增强总体规划

> 制定日期: 2026-05-24 | 基于全面 Gap 分析

---

## 一、增强优先级排序

### 1.1 严重程度分级

| 级别 | starter | 当前文件数 | 核心问题 |
|------|---------|-----------|----------|
| **P0 严重** | starter-sms | 10 | SDK 实现全是空壳/stub，生产不可用 |
| **P0 严重** | starter-seata | 3 | 仅 1 个 bean 透传，缺乏事务监控能力 |
| **P1 高** | starter-job | 5 | 无重试、无指标、无事件、无 Actuator |
| **P1 高** | starter-idempotent | 8 | 硬编码 Redis、无存储 SPI、无指标 |
| **P2 中** | starter-sentinel | 10 | 仅支持 Nacos 数据源、无响应式支持、无指标桥接 |
| **P2 中** | starter-feign | 11 | 无重试、无指标、无健康检查（已有 1 个测试） |

### 1.2 公共缺失能力（所有薄 starter 都缺）

| 能力 | 说明 | 参考实现 |
|------|------|----------|
| **HealthIndicator** | 每个涉及外部依赖的 starter 必须有 | `starter-monitor/NacosHealthIndicator` |
| **Micrometer Metrics** | 计数器、计时器、连接池 gauge | `starter-monitor/BusinessMetrics` |
| **Spring Event** | 关键操作的事件发布，支持审计和告警 | `common-log/OperateLogEvent` |
| **单元测试** | AutoConfiguration 测试 + 核心组件测试 | `starter-feign/FeignAutoConfigurationTest` |

---

## 二、各 Starter 增强方案

### 2.1 starter-sms（P0 — 空壳实现，必须重写）

**当前状态**：`AliyunSmsClient` 和 `TencentSmsClient` 都是 stub，不调用真实 SDK。

**增强目标**：

| 优先级 | 增强项 | 新增文件 | 说明 |
|--------|--------|----------|------|
| P0 | 真实 SDK 集成 | `core/AliyunSmsClient.java`（重写） | 使用 `com.aliyun:dysmsapi20170525` SDK |
| P0 | 真实 SDK 集成 | `core/TencentSmsClient.java`（重写） | 使用 `com.tencentcloudapi:tencentcloud-sdk-java-sms` SDK |
| P0 | @ConditionalOnClass 修复 | `config/SmsAutoConfiguration.java`（修改） | 增加 SDK 类存在性检查 |
| P1 | HealthIndicator | `health/SmsHealthIndicator.java` | 检查凭证有效性、SDK 连通性 |
| P1 | Metrics | `metrics/SmsMetrics.java` | `sms.send.total`, `sms.send.success`, `sms.send.failure`, `sms.send.duration` |
| P1 | 事件发布 | `event/SmsSentEvent.java`, `event/SmsFailedEvent.java` | 发送成功/失败事件 |
| P2 | 重试机制 | `core/RetryableSmsClient.java` | 可配置重试次数和退避策略 |
| P2 | 号码校验 | `util/PhoneValidator.java` | 发送前校验手机号格式 |
| P2 | 限流 | `core/SmsRateLimiter.java` | 客户端限流，防止触发服务商限制 |
| P3 | 测试 | `config/SmsAutoConfigurationTest.java` | 条件装配验证 |

**目标文件数**：10 → 18（+8 文件）

**依赖变更**：POM 需要新增阿里云/腾讯云 SMS SDK 依赖（optional）

---

### 2.2 starter-seata（P0 — 过于单薄）

**当前状态**：仅 `SeataAutoConfiguration` + `SeataProperties`，注册 1 个 `GlobalTransactionScanner`。

**增强目标**：

| 优先级 | 增强项 | 新增文件 | 说明 |
|--------|--------|----------|------|
| P1 | HealthIndicator | `health/SeataHealthIndicator.java` | 检查 TC 连接状态、txServiceGroup |
| P1 | xid 追踪桥接 | `tracing/SeataTracingFilter.java` | 将 Seata xid 写入 MDC，支持全链路追踪 |
| P1 | 事务事件 | `event/GlobalTransactionEvent.java` | 事务开始/提交/回滚/超时事件 |
| P1 | 事务事件 | `event/SeataTransactionEventListener.java` | 实现 TransactionHook，发布 Spring Event |
| P2 | SPI 扩展 | `spi/SeataCustomizer.java` | 允许自定义 GlobalTransactionScanner 配置 |
| P2 | Metrics | `metrics/SeataMetrics.java` | `seata.transaction.total/commit/rollback/timeout/duration` |
| P3 | 测试 | `config/SeataAutoConfigurationTest.java` | 条件装配验证 |

**目标文件数**：3 → 10（+7 文件）

---

### 2.3 starter-job（P1 — 缺乏生产级能力）

**当前状态**：`JobAutoConfiguration` + `JobProperties` + `AbstractJobHandler`，仅做 executor 注册。

**增强目标**：

| 优先级 | 增强项 | 新增文件 | 说明 |
|--------|--------|----------|------|
| P1 | HealthIndicator | `health/JobHealthIndicator.java` | 检查 Admin 连通性、executor 注册状态 |
| P1 | 事件发布 | `event/JobExecutionEvent.java` | Job 执行事件（handler 名、耗时、成功/失败、异常） |
| P1 | Metrics | `metrics/JobMetrics.java` | `job.execution.total/success/failure/duration` |
| P1 | 执行拦截器 SPI | `spi/JobInterceptor.java` | `beforeExecute` / `afterExecute` 钩子接口 |
| P1 | 执行拦截器 SPI | `spi/JobInterceptorChain.java` | 拦截器链管理 |
| P2 | 重试支持 | `annotation/RetryableJob.java` | `maxAttempts`, `backoffDelay` 属性 |
| P2 | 重试支持 | `core/RetryableJobHandler.java` | 装饰器，包装任意 JobHandler 实现重试 |
| P2 | 上下文传播 | `core/JobContextPropagator.java` | UserContext/TenantContext 传播 |
| P2 | Actuator Endpoint | `endpoint/JobEndpoint.java` | 暴露已注册 handler 列表、最近执行统计 |
| P3 | 测试 | `config/JobAutoConfigurationTest.java` | 条件装配验证 |

**目标文件数**：5 → 15（+10 文件）

---

### 2.4 starter-idempotent（P1 — 硬编码 Redis）

**当前状态**：幂等切面硬编码使用 `StringRedisTemplate`，无存储抽象。

**增强目标**：

| 优先级 | 增强项 | 新增文件 | 说明 |
|--------|--------|----------|------|
| P1 | 存储 SPI | `spi/IdempotentStore.java` | `tryAcquire(key, requestId, timeout)`, `release(key, requestId)`, `consume(key)` |
| P1 | 存储 SPI | `spi/RedisIdempotentStore.java` | 从 `IdempotentAspect` 抽取 Redis 实现 |
| P1 | 键策略 SPI | `spi/IdempotentKeyStrategy.java` | `buildKey(joinPoint, annotation)` 接口 |
| P1 | 键策略 SPI | `spi/DefaultIdempotentKeyStrategy.java` | 当前逻辑的默认实现 |
| P1 | Metrics | `metrics/IdempotentMetrics.java` | `idempotent.lock.acquired/rejected/timeout`, `idempotent.token.created/consumed/rejected` |
| P1 | 事件发布 | `event/IdempotentRejectedEvent.java` | 重复请求被拒绝时发布事件 |
| P2 | Token 注解 | `annotation/IdempotentToken.java` | 方法级注解，自动生成并注入幂等 Token |
| P2 | Token 注解 | `aspect/IdempotentTokenAspect.java` | 拦截 `@IdempotentToken`，自动注入 token |
| P2 | HealthIndicator | `health/IdempotentHealthIndicator.java` | 检查 Redis 连通性 |
| P3 | 测试 | `aspect/IdempotentAspectTest.java` | 核心切面逻辑测试 |

**目标文件数**：8 → 18（+10 文件）

---

### 2.5 starter-sentinel（P2 — 数据源单一、无指标）

**当前状态**：仅支持 Nacos 数据源，仅 Servlet，无 Micrometer 桥接。

**增强目标**：

| 优先级 | 增强项 | 新增文件 | 说明 |
|--------|--------|----------|------|
| P1 | HealthIndicator | `health/SentinelHealthIndicator.java` | 检查 slots 初始化、数据源连通性、已加载规则数 |
| P2 | Metrics 桥接 | `metrics/SentinelMetrics.java` | `sentinel.block/pass/exception.count`, `sentinel.rt` |
| P2 | Metrics 桥接 | `metrics/SentinelMetricsAutoConfig.java` | 定时采集 Sentinel 内部指标到 Micrometer |
| P2 | 响应式支持 | `handler/ReactiveSentinelBlockExceptionHandler.java` | WebFlux 下的限流/熔断异常处理 |
| P2 | 数据源策略 | `datasource/DataSourceConfigStrategy.java` | 数据源配置策略接口 |
| P2 | 数据源策略 | `datasource/FileDataSourceConfig.java` | 文件数据源配置 |
| P2 | Dashboard 配置 | `config/SentinelTransportConfig.java` | Dashboard 地址、传输端口配置 |
| P3 | Block 响应 SPI | `spi/BlockResponseCustomizer.java` | 自定义限流响应格式 |
| P3 | 测试 | `config/SentinelAutoConfigurationTest.java` | 条件装配验证 |

**目标文件数**：10 → 19（+9 文件）

---

### 2.6 starter-feign（P2 — 缺少监控和重试）

**当前状态**：已有拦截器/解码器/降级，但缺指标、重试、健康检查。

**增强目标**：

| 优先级 | 增强项 | 新增文件 | 说明 |
|--------|--------|----------|------|
| P1 | Metrics | `metrics/FeignMetricsInterceptor.java` | `feign.call.total/duration/error` per service#method |
| P1 | Metrics | `metrics/OkHttpConnectionPoolMetrics.java` | `feign.okhttp.connections.idle/active` gauge |
| P2 | HealthIndicator | `health/FeignHealthIndicator.java` | 已注册 Feign 客户端连通性汇总 |
| P2 | 重试机制 | `retry/FeignRetryer.java` | 可配置 `maxAttempts`, `backoffMs`, `retryableStatuses` |
| P2 | 请求日志 | `logging/FeignBodyLoggingFilter.java` | DEBUG 级别的请求/响应体日志 |
| P2 | 熔断集成 | `config/SentinelFeignAutoConfiguration.java` | `sentinelEnabled=true` 时自动集成 Sentinel |
| P3 | 每客户端超时 | `config/PerClientTimeoutConfig.java` | 不同 Feign 客户端可配置不同超时 |
| P3 | 测试补充 | `decoder/FeignResponseDecoderTest.java` | 解码器测试 |
| P3 | 测试补充 | `decoder/FeignErrorDecoderTest.java` | 错误解码器测试 |
| P3 | 测试补充 | `config/FeignRequestInterceptorTest.java` | 请求拦截器测试 |

**目标文件数**：11 → 22（+11 文件）

---

## 三、增强后的文件数预估

| Starter | 当前 | 增强后 | 增幅 |
|---------|------|--------|------|
| starter-sms | 10 | 18 | +8 |
| starter-seata | 3 | 10 | +7 |
| starter-job | 5 | 15 | +10 |
| starter-idempotent | 8 | 18 | +10 |
| starter-sentinel | 10 | 19 | +9 |
| starter-feign | 11 | 22 | +11 |
| **合计** | **47** | **102** | **+55** |

增强后 6 个 starter 平均 17 文件，与当前中等 starter（es=22, mybatis=20, auth=19）持平。

---

## 四、统一增强模式

每个增强的 starter 都遵循统一的包结构模式：

```
com.sloth.boot.starter.<module>/
  ├── config/           # AutoConfiguration + Properties
  ├── core/             # 核心实现
  ├── spi/              # 扩展点接口 + 默认实现
  ├── event/            # Spring Event 定义
  ├── health/           # HealthIndicator
  ├── metrics/          # Micrometer Metrics
  ├── annotation/       # 自定义注解（按需）
  ├── aspect/           # AOP 切面（按需）
  └── package-info.java # 每个包的文档
```

---

## 五、与 30 天路线图的集成

Starter 增强需要纳入现有路线图的**阶段四（Day 16-21）**，同时部分工作可并行到其他阶段：

| 天数 | 原计划 | 新增 Starter 增强 |
|------|--------|-------------------|
| Day 16 | starter-redis 幂等剥离 | **+** starter-idempotent 存储 SPI 设计 |
| Day 17 | OSS/SMS 条件装配修复 | **+** starter-sms 真实 SDK 集成 |
| Day 18 | MyBatis 条件装配修复 | **+** starter-job 事件/拦截器/Metrics |
| Day 19 | Monitor BeanPostProcessor 移除 | **+** starter-seata 健康检查/事件/追踪 |
| Day 20 | Gateway Reactive MDC | **+** starter-sentinel 响应式/指标/健康检查 |
| Day 21 | starter 全量验证 | **+** starter-feign 指标/重试/健康检查 |
| Day 23 | 测试补充（common-core） | **+** 6 个 starter 的 AutoConfiguration 测试 |
| Day 24 | 测试补充（starter-web/redis） | **+** 6 个 starter 的核心组件测试 |

---

## 六、风险与约束

| 风险 | 影响 | 缓解 |
|------|------|------|
| SMS SDK 依赖引入增加 POM 体积 | 中 | SDK 依赖标记 `<optional>true</optional>` |
| 增强工作量超出单日承载 | 高 | P0/P1 优先，P2/P3 可延后到下一周期 |
| Seata SDK 版本兼容性 | 低 | Seata 2.0.0 已在 dependencies BOM 中锁定 |
| 增强导致已有用法不兼容 | 中 | 所有新 bean 标注 `@ConditionalOnMissingBean`，保持向后兼容 |
| 30 天时间紧张 | 高 | 优先完成 P0+P1，P2/P3 记录在案，可在后续版本迭代 |
