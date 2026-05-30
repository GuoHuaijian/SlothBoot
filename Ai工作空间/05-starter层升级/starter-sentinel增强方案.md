# starter-sentinel 增强方案

> 优先级: P2 | 当前: 10 文件 | 目标: 19 文件

---

## 一、当前问题

1. **仅支持 Nacos 数据源** — 不支持 Apollo、ZooKeeper、文件、JDBC
2. **无响应式支持** — 只有 `@ConditionalOnWebApplication(SERVLET)` 处理器
3. **无 Micrometer 桥接** — Sentinel 内部指标无法导出到监控系统
4. **无健康检查** — 不知道 Sentinel 是否初始化成功、数据源是否连通
5. **无 Dashboard 配置** — 缺少 Sentinel Dashboard 连接配置
6. **Block 响应不可定制** — 硬编码中文消息

---

## 二、增强方案

### 2.1 HealthIndicator（P1）

```
health/SentinelHealthIndicator.java
- 检查: Env.slots 是否已初始化
- 检查: 数据源连通性（Nacos → 尝试读取规则、File → 检查文件存在）
- 报告: datasourceType, loadedRulesCount(flow/degrade/system/paramFlow), dashboardConnected
```

### 2.2 Metrics 桥接（P2）

```
metrics/SentinelMetrics.java
- @Scheduled 定时采集（每 5 秒）
- 从 Sentinel 的 MetricStorage 或 DefaultNode 读取
- 计数器: sentinel.block.count (tags: resource, ruleType)
- 计数器: sentinel.pass.count (tags: resource)
- 计数器: sentinel.exception.count (tags: resource)
- 计时器: sentinel.rt (tags: resource)
- 条件: @ConditionalOnClass(MeterRegistry.class)

metrics/SentinelMetricsAutoConfig.java
- 注册 SentinelMetrics bean
- 条件: @ConditionalOnProperty(prefix = "sloth.sentinel.metrics", name = "enabled")
```

### 2.3 响应式支持（P2）

```
handler/ReactiveSentinelBlockExceptionHandler.java
- 实现 Sentinel 的 BlockExceptionHandler
- 在 WebFlux 环境下处理限流/熔断异常
- 返回 JSON 格式的 R.fail 响应
- 条件: @ConditionalOnWebApplication(REACTIVE)
```

### 2.4 数据源策略（P2）

```
datasource/DataSourceConfigStrategy.java
- 接口: void register(Environment env, SentinelProperties props)

datasource/FileDataSourceConfig.java
- 实现: 注册 FileRefreshableDataSource 用于流控/降级规则
- 配置: sloth.sentinel.datasource.file.flow-rules-path, degrade-rules-path
```

AutoConfiguration 中根据 `sloth.sentinel.datasource.type` 选择策略。

### 2.5 Dashboard 配置（P2）

```
config/SentinelTransportConfig.java
- 读取 SentProperties.dashboardAddress, transportPort
- 设置系统属性: csp.sentinel.dashboard.server, csp.sentinel.api.port
- @PostConstruct 初始化
```

### 2.6 Block 响应 SPI（P3）

```
spi/BlockResponseCustomizer.java
- 接口: R<Void> customize(String resourceName, BlockException exception)
- 默认实现: 当前 GlobalBlockHandler 逻辑
- 链式: 收集所有 customizer bean，依次执行
```

---

## 三、新增文件清单

| 文件 | 类型 | 说明 |
|------|------|------|
| `health/SentinelHealthIndicator.java` | 新增 | 健康检查 |
| `metrics/SentinelMetrics.java` | 新增 | Micrometer 桥接 |
| `metrics/SentinelMetricsAutoConfig.java` | 新增 | 指标自动配置 |
| `handler/ReactiveSentinelBlockExceptionHandler.java` | 新增 | 响应式异常处理 |
| `datasource/DataSourceConfigStrategy.java` | 新增 | 数据源策略接口 |
| `datasource/FileDataSourceConfig.java` | 新增 | 文件数据源 |
| `config/SentinelTransportConfig.java` | 新增 | Dashboard 连接配置 |
| `spi/BlockResponseCustomizer.java` | 新增 | Block 响应定制 SPI |
| `config/SentinelAutoConfiguration.java` | 修改 | 注册新 bean |
| `config/SentinelProperties.java` | 修改 | 增加新配置项 |
