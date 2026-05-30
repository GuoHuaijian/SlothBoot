# starter-idempotent 增强方案

> 优先级: P1 | 当前: 8 文件 | 目标: 18 文件

---

## 一、当前问题

1. **硬编码 Redis** — `IdempotentAspect` 直接使用 `StringRedisTemplate`，无法替换存储
2. **无存储 SPI** — 企业级应支持数据库、内存等多种幂等存储
3. **键构建逻辑封闭** — 无法自定义幂等 Key 生成策略
4. **无指标** — 不知道有多少请求被幂等拒绝
5. **无拒绝事件** — 被拒绝的重复请求无法审计
6. **Token 端需手动调用** — 无注解自动生成 Token
7. **无健康检查**

---

## 二、增强方案

### 2.1 存储 SPI（P1）

```
spi/IdempotentStore.java
- 接口:
    boolean tryAcquire(String key, String requestId, Duration timeout)
    boolean release(String key, String requestId)
    boolean consumeToken(String key)

spi/RedisIdempotentStore.java
- 从 IdempotentAspect 中抽取 Redis 实现
- 使用 StringRedisTemplate + Lua 脚本
- @ConditionalOnMissingBean(IdempotentStore.class)
```

**IdempotentAspect 重构**：注入 `IdempotentStore` 替代 `StringRedisTemplate`。

### 2.2 键策略 SPI（P1）

```
spi/IdempotentKeyStrategy.java
- 接口: String buildKey(ProceedingJoinPoint joinPoint, Idempotent annotation)

spi/DefaultIdempotentKeyStrategy.java
- 默认实现: 类名#方法名 + SpEL 表达式求值
- @ConditionalOnMissingBean
```

### 2.3 Metrics（P1）

```
metrics/IdempotentMetrics.java
- 注入 MeterRegistry
- 计数器: idempotent.lock.acquired (tags: key, method)
- 计数器: idempotent.lock.rejected (tags: key, method)
- 计数器: idempotent.lock.timeout (tags: key, method)
- 计数器: idempotent.token.created (tags: method)
- 计数器: idempotent.token.consumed (tags: method)
- 计数器: idempotent.token.rejected (tags: method)
```

### 2.4 拒绝事件（P1）

```
event/IdempotentRejectedEvent.java
- 继承 BaseEvent
- 字段: key, methodSignature, mode(LOCK/TOKEN), userId, clientIp, timestamp
- 发布位置: IdempotentAspect 中锁获取失败或 Token 校验失败时
```

### 2.5 Token 注解（P2）

```
annotation/IdempotentToken.java
- @Target(METHOD), @Retention(RUNTIME)
- 属性: headerName="X-Idempotent-Token"（可选，支持注入到 header 或 model）

aspect/IdempotentTokenAspect.java
- @Around 拦截 @IdempotentToken 方法
- 调用 TokenIdempotentService.createToken()
- 将 token 注入到 HttpServletRequest header 或方法返回值的 model 属性中
```

### 2.6 HealthIndicator（P2）

```
health/IdempotentHealthIndicator.java
- 检查: IdempotentStore.isAvailable()
- Redis 实现: PING 命令
- 报告: storeType, keyPrefix, timeout
```

---

## 三、新增文件清单

| 文件 | 类型 | 说明 |
|------|------|------|
| `spi/IdempotentStore.java` | 新增 | 存储抽象接口 |
| `spi/RedisIdempotentStore.java` | 新增 | Redis 实现（从 Aspect 抽取） |
| `spi/IdempotentKeyStrategy.java` | 新增 | 键策略接口 |
| `spi/DefaultIdempotentKeyStrategy.java` | 新增 | 默认键策略 |
| `metrics/IdempotentMetrics.java` | 新增 | Micrometer 指标 |
| `event/IdempotentRejectedEvent.java` | 新增 | 拒绝事件 |
| `annotation/IdempotentToken.java` | 新增 | Token 自动注入注解 |
| `aspect/IdempotentTokenAspect.java` | 新增 | Token 注解切面 |
| `health/IdempotentHealthIndicator.java` | 新增 | 健康检查 |
| `aspect/IdempotentAspect.java` | 重构 | 依赖 IdempotentStore + IdempotentKeyStrategy |
| `config/IdempotentAutoConfiguration.java` | 修改 | 注册新 bean |
