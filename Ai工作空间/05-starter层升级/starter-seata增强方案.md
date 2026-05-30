# starter-seata 增强方案

> 优先级: P0 | 当前: 3 文件 | 目标: 10 文件

---

## 一、当前问题

1. **仅 1 个 bean** — 只注册 `GlobalTransactionScanner`，等同于 Seata 自身的 starter 加一行默认配置
2. **无健康检查** — 无法通过 Actuator 确认 TC 是否连通
3. **无事务可观测** — 事务提交/回滚/超时无任何指标和事件
4. **xid 不进 MDC** — 分布式事务 ID 不出现在日志中，无法关联事务链路
5. **无扩展点** — 无法自定义 GlobalTransactionScanner 配置

---

## 二、增强方案

### 2.1 Seata xid 追踪桥接（P1）

```
tracing/SeataTracingFilter.java
- Servlet Filter (OncePerRequestFilter)
- 读取 RootContext.getXID()
- 若存在，写入 MDC.put("seataXid", xid)
- 在 finally 中清理
- 日志 pattern 中增加 %X{seataXid}
```

### 2.2 事务事件（P1）

```
event/GlobalTransactionEvent.java
- 继承 BaseEvent
- 字段: xid, status(BEGIN/COMMIT/ROLLBACK/TIMEOUT), branchType, beginTime, endTime, costTime

event/SeataTransactionEventListener.java
- 实现 io.seata.tm.api.transaction.TransactionHook
- onBegin: 发布 GlobalTransactionEvent(status=BEGIN)
- onCommit: 发布 GlobalTransactionEvent(status=COMMIT)
- onRollback: 发布 GlobalTransactionEvent(status=ROLLBACK)
- 注册方式: 在 GlobalTransactionScanner 创建后自动 attach
```

### 2.3 HealthIndicator（P1）

```
health/SeataHealthIndicator.java
- 检查: GlobalTransactionScanner bean 是否存在
- 检查: RootContext 是否可用
- 检查: seata.tx-service-group 配置是否正确
- 报告: txServiceGroup, mode, 配置状态
- 注: 不直接 ping TC（需要 Netty 依赖），改为检查本地状态
```

### 2.4 Metrics（P2）

```
metrics/SeataMetrics.java
- 注入 MeterRegistry
- 计数器: seata.transaction.total, seata.transaction.commit, seata.transaction.rollback, seata.transaction.timeout
- 计时器: seata.transaction.duration
- 数据来源: 通过 TransactionHook 在事务生命周期中累加
```

### 2.5 SPI 扩展（P2）

```
spi/SeataCustomizer.java
- @FunctionalInterface
- void customize(GlobalTransactionScanner scanner)
- 注册方式: 收集所有 SeataCustomizer bean，在 AutoConfiguration 中应用
```

---

## 三、新增文件清单

| 文件 | 类型 | 说明 |
|------|------|------|
| `tracing/SeataTracingFilter.java` | 新增 | xid → MDC 桥接 |
| `event/GlobalTransactionEvent.java` | 新增 | 事务生命周期事件 |
| `event/SeataTransactionEventListener.java` | 新增 | TransactionHook 实现，发布事件 |
| `health/SeataHealthIndicator.java` | 新增 | 健康检查 |
| `metrics/SeataMetrics.java` | 新增 | Micrometer 指标 |
| `spi/SeataCustomizer.java` | 新增 | 扩展点接口 |
| `config/SeataAutoConfiguration.java` | 修改 | 注册新 bean |
| `config/SeataProperties.java` | 修改 | 增加新配置项 |
