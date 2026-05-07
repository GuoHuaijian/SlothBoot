# Sloth Boot 架构文档

## 1. 模块依赖全景图

```text
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              sloth-boot (root aggregator)                       │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌───────────────────────┐    ┌──────────────────────┐                          │
│  │ sloth-boot-dependencies│    │  sloth-boot-parent   │                          │
│  │      (BOM)            │◄───│   (shared parent)    │                          │
│  │  版本集中管理           │    │  Lombok/MapStruct/   │                          │
│  └───────────────────────┘    │  Jackson/Hutool/...  │                          │
│                               └──────────┬───────────┘                          │
│                                          │                                      │
│                    ┌─────────────────────┼─────────────────────┐                │
│                    │                     │                     │                │
│           ┌────────▼────────┐   ┌────────▼────────┐   ┌───────▼────────┐       │
│           │ sloth-boot-common│   │sloth-boot-starter│   │sloth-boot-     │       │
│           │   (基础层)       │   │  (能力层)        │   │generator(工具) │       │
│           └────────┬────────┘   └────────┬────────┘   └────────────────┘       │
│                    │                     │                                      │
│    ┌───────┬───────┼───────┬─────┐       │                                      │
│    │       │       │       │     │       │                                      │
│  core    log   security  doc  test   ┌───┴─────────────────────────────────┐    │
│                                      │                                     │    │
│                                   ┌──┴──┬──────┬──────┬──────┬──────┐     │    │
│                                   │     │      │      │      │      │     │    │
│                                 web   redis  mybatis  ai    auth  ...    │    │
│                                   │     │      │      │             17个  │    │
│                                   └─────┴──────┴──────┴─────────────────┘    │
│                                                                              │
│           ┌────────────────────────────────────────────┐                     │
│           │         sloth-boot-example-service          │                     │
│           │  (消费所有模块，可运行的示例工程)               │                     │
│           └────────────────────────────────────────────┘                     │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 依赖流向规则

```text
common-core  ◄──  所有 starter 都依赖
common-log   ◄──  starter-web 等需要日志的模块
common-security ◄── starter-web（XSS/脱敏）

starter-*    ◄──  example-service（按需引入）
generator    ◄──  独立使用，不被其他模块依赖
```

**核心原则：**
- `common-core` 是最底层，不依赖任何 starter
- `common-*` 之间不相互依赖
- `starter-*` 只依赖 `common-core`（及需要的 common 子模块），不依赖其他 starter
- `example` 按需引入 starter，演示集成效果

---

## 2. 请求处理流程图

### 2.1 标准 HTTP 请求流程

```text
Client Request
      │
      ▼
┌─────────────┐
│   Gateway    │  (可选，微服务模式)
│  过滤器链:    │
│  ├ TraceId   │  ── 生成/传递 traceId
│  ├ Auth      │  ── Token 校验，解析用户信息
│  ├ BlackList │  ── IP 黑名单
│  └ Log       │  ── 请求日志
└──────┬──────┘
       │  HTTP Headers: X-Trace-Id, X-User-Id, X-Username, X-Tenant-Id
       ▼
┌─────────────┐
│  starter-web │
│  过滤器链:    │
│  ├ TraceFilter│ ── 设置 TraceContext
│  ├ RequestLog│ ── 请求/响应日志
│  ├ XSS Filter│ ── XSS 清洗
│  └ UserCtx   │ ── 从 Header 解析 UserContext
│  Interceptor │
│  ├ UserCtx   │ ── 设置 UserContext
│  └ CORS      │ ── 跨域处理
└──────┬──────┘
       ▼
┌─────────────┐
│  Controller  │
│  ├ @RateLimit│ ── Redis 滑动窗口限流
│  ├ @Idempotent│── Redis 幂等校验
│  ├ @DistribLock│── Redisson 分布式锁
│  └ @OperateLog│ ── 操作日志 AOP
└──────┬──────┘
       ▼
┌─────────────┐
│   Service    │
│  (业务逻辑)   │
└──────┬──────┘
       ▼
┌─────────────┐
│    Mapper    │  (MyBatis-Plus)
│  ├ AutoFill  │ ── 自动填充 createBy/updateBy/createTime/updateTime
│  ├ DataScope │ ── 数据权限 WHERE 注入
│  ├ TenantLine│ ── 租户隔离
│  └ SlowSQL   │ ── 慢 SQL 告警
└──────┬──────┘
       ▼
┌─────────────┐
│  统一响应包装  │  GlobalResponseAdvice
│  R<T> {      │
│    code, msg,│
│    data,     │
│    traceId,  │
│    timestamp │
│  }           │
└──────┬──────┘
       ▼
   Client Response
```

### 2.2 异常处理流程

```text
Controller/Service 抛出异常
       │
       ▼
┌─────────────────────┐
│ GlobalExceptionHandler│
│                      │
│  BizException     ──► R.fail(code, msg)     业务异常
│  SystemException  ──► R.fail(500, msg)      系统异常
│  ValidationEx     ──► R.fail(400, 字段错误)  参数校验
│  MethodNotAllowed ──► R.fail(405)           方法不支持
│  NoHandler        ──► R.fail(404)           路径不存在
│  MaxUpload        ──► R.fail(400, 文件过大)  上传限制
│  Exception        ──► R.fail(500)           兜底处理
└─────────────────────┘
```

### 2.3 认证授权流程

```text
请求到达
   │
   ▼
白名单路径？ ──是──► 放行
   │否
   ▼
黑名单路径？ ──是──► 拒绝 (403)
   │否
   ▼
携带 Token？ ──否──► 未认证 (401)
   │是
   ▼
Token 有效？ ──否──► 过期/无效 (401)
   │是
   ▼
Sa-Token 登录态校验
   │
   ▼
同步到 UserContext (userId, username, tenantId, roles)
   │
   ▼
权限/角色注解校验 (@SaCheckPermission / @SaCheckRole)
   │
   ▼
进入 Controller
```

---

## 3. 自动装配原理

### 3.1 装配机制

```text
Spring Boot 3.x 自动装配
       │
       ▼
META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
       │
       ▼
每个 starter 注册自己的 AutoConfiguration 类
       │
       ▼
条件注解控制是否生效:
├── @ConditionalOnClass        ── 类路径上有指定类
├── @ConditionalOnMissingBean  ── 容器中没有同类型 Bean（支持覆盖）
├── @ConditionalOnProperty     ── 配置项满足条件
└── @ConditionalOnWebApplication ── Web 应用类型匹配
```

### 3.2 配置绑定

```text
application.yml                    ConfigurationProperties
      │                                    │
      ▼                                    ▼
sloth.web.response-wrapper=true ──► SlothWebProperties.responseWrapper
sloth.redis.key-prefix="app:"  ──► RedisProperties.keyPrefix
sloth.auth.token-timeout=7200  ──► AuthProperties.tokenTimeout
      │
      ▼
IDE 自动补全 (additional-spring-configuration-metadata.json)
```

### 3.3 用户覆盖机制

```java
// 框架默认注册
@Bean
@ConditionalOnMissingBean
public DistributedLock distributedLock(RedissonClient client) {
    return new RedissonDistributedLock(client);
}

// 业务侧覆盖（只需声明同类型 Bean）
@Bean
public DistributedLock distributedLock() {
    return new MyCustomDistributedLock();  // 框架的 Bean 不会注册
}
```

---

## 4. 部署架构图

### 4.1 单体部署（推荐入门）

```text
                    ┌──────────────────────┐
                    │     Nginx / LB       │
                    └──────────┬───────────┘
                               │
                    ┌──────────▼───────────┐
                    │   Sloth Boot App     │
                    │   (example-service)  │
                    │   Port: 8080         │
                    └──────┬───────┬───────┘
                           │       │
                ┌──────────▼──┐ ┌──▼──────────┐
                │   MySQL 8.0 │ │  Redis 7.x  │
                │   Port:3306 │ │  Port:6379  │
                └─────────────┘ └─────────────┘
```

### 4.2 微服务部署（Spring Cloud Alibaba）

```text
                    ┌──────────────────────┐
                    │     Nginx / SLB      │
                    └──────────┬───────────┘
                               │
                    ┌──────────▼───────────┐
                    │  Spring Cloud Gateway │
                    │  (sloth-starter-     │
                    │   gateway)           │
                    └──────────┬───────────┘
                               │
              ┌────────────────┼────────────────┐
              │                │                │
    ┌─────────▼─────────┐ ┌───▼──────────┐ ┌───▼──────────┐
    │   用户服务          │ │  订单服务     │ │  支付服务     │
    │   (sloth-example)  │ │              │ │              │
    │   + starter-auth   │ │              │ │              │
    │   + starter-mybatis│ │              │ │              │
    │   + starter-redis  │ │              │ │              │
    └─────────┬─────────┘ └──────┬───────┘ └──────┬───────┘
              │                   │                │
    ┌─────────┼───────────────────┼────────────────┘
    │         │                   │
    │  ┌──────▼──────┐  ┌────────▼────────┐  ┌──────────────┐
    │  │  Nacos       │  │  Sentinel        │  │  RocketMQ    │
    │  │  注册/配置    │  │  限流/降级        │  │  消息队列     │
    │  └─────────────┘  └─────────────────┘  └──────────────┘
    │
    ├── MySQL (主从)
    ├── Redis (集群)
    ├── Seata (分布式事务)
    └── XXL-Job (任务调度)
```

### 4.3 Docker Compose 部署

```text
docker-compose up -d
       │
       ├── sloth-example    (应用服务, :8080)
       ├── mysql            (数据库,   :3306)
       └── redis            (缓存,     :6379)
```

---

## 5. 数据流转图

### 5.1 上下文传播

```text
HTTP 请求
   │
   ▼ TraceFilter
TraceContext.set(traceId)     ─── ThreadLocal (TTL)
   │
   ▼ UserContextInterceptor
UserContext.set(userId,       ─── ThreadLocal (TTL)
               username,
               tenantId,
               roles)
   │
   ▼ Feign 调用
FeignRequestInterceptor       ─── Header 透传
   │  X-Trace-Id
   │  X-User-Id
   │  X-Username
   │  X-Tenant-Id
   ▼
下游服务 TraceFilter          ─── 重新设置 TraceContext
   │
   ▼ @Async / 线程池
TtlTaskDecorator              ─── TTL 自动传递上下文
   │
   ▼ 子线程中
TraceContext.get()            ─── 获取到 traceId ✅
UserContext.get()             ─── 获取到 userId ✅
```

### 5.2 分布式追踪集成

```text
Micrometer Tracing (OpenTelemetry)
       │
       ▼
TraceContextBridge.syncToTraceContext()
       │
       ▼
TraceContext.set(traceId, spanId)  ─── 与自定义 TraceContext 同步
       │
       ▼
日志中输出 traceId  ─── logback-spring.xml 使用 %X{traceId}
```

---

## 6. 模块选型指南

### 6.1 按场景选择 Starter

| 场景 | 推荐模块 | 说明 |
|------|---------|------|
| REST API 开发 | `starter-web` + `starter-mybatis` + `starter-redis` | 基础三件套 |
| 认证授权 | `starter-auth` | Sa-Token 集成 |
| 微服务网关 | `starter-gateway` + `starter-auth` | Gateway + 认证 |
| 服务间调用 | `starter-feign` | Feign + Header 透传 |
| 限流降级 | `starter-sentinel` | Sentinel + Nacos 规则 |
| 分布式事务 | `starter-seata` | Seata AT 模式 |
| 消息队列 | `starter-mq` | RocketMQ 封装 |
| 定时任务 | `starter-job` | XXL-Job 集成 |
| AI 能力 | `starter-ai` | Spring AI 多模型 |
| 文件存储 | `starter-oss` | 本地/MinIO/阿里云 |
| Excel 操作 | `starter-excel` | EasyExcel 封装 |
| 短信发送 | `starter-sms` | 阿里云/腾讯云 |
| 搜索引擎 | `starter-es` | Elasticsearch 封装 |
| 监控告警 | `starter-monitor` | 健康检查/告警/指标 |

### 6.2 最小化引入

```xml
<!-- 最精简：只需要统一返回和异常处理 -->
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-starter-web</artifactId>
</dependency>

<!-- 标准 Web 应用 -->
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-starter-mybatis</artifactId>
</dependency>
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-starter-redis</artifactId>
</dependency>
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-starter-auth</artifactId>
</dependency>

<!-- 完整微服务 -->
<!-- 在标准 Web 基础上加上: -->
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-starter-monitor</artifactId>
</dependency>
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-starter-thread-pool</artifactId>
</dependency>
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-common-doc</artifactId>
</dependency>
```
