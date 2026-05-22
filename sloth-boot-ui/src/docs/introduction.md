# 项目简介

## Sloth Boot 是什么？

Sloth Boot 是基于 **Spring Boot 3.5 + Spring Cloud 2025 + Spring Cloud Alibaba** 的企业级微服务开发脚手架。

> **慢工出细活。** 不造平台，只沉淀高频、可复用的工程化基础能力。

### 设计理念

- **模块化**：24 个模块按需引入，避免臃肿依赖
- **统一配置**：所有能力通过 `sloth.*` 命名空间统一管理
- **自动装配**：`@ConditionalOnMissingBean` SPI 设计，零侵入扩展
- **开箱即用**：分布式锁、限流、幂等、缓存等高频场景直接使用
- **代码可读**：注释友好，适合学习和二次定制

## 技术栈

| 层次 | 技术选型 |
|------|---------|
| **语言 & 运行时** | Java 21 · Maven 3.8.1+ |
| **核心框架** | Spring Boot 3.5.0 · Spring Cloud 2025.1.1 |
| **微服务生态** | Spring Cloud Alibaba 2025.1.0.0 · Nacos · Sentinel · Seata |
| **AI 能力** | Spring AI 1.1.6 · OpenAI · 通义千问 · DeepSeek · Ollama |
| **数据层** | MyBatis-Plus · MySQL · Elasticsearch |
| **缓存 & 消息** | Redis · RocketMQ |
| **任务 & 文件** | XXL-Job · MinIO · 阿里云 OSS |
| **网关 & RPC** | Spring Cloud Gateway · OpenFeign |
| **文档 & 监控** | Knife4j · Spring Actuator · Prometheus |

## 模块总览

### Common 基础层

| 模块 | 核心能力 |
|------|---------|
| `sloth-boot-common-core` | 统一返回体 `R<T>`、全局异常体系、错误码、请求上下文、工具类、通用注解、i18n |
| `sloth-boot-common-log` | TraceId 过滤器、HTTP 请求日志、`@OperationLog` 操作日志切面 |
| `sloth-boot-common-security` | AES/RSA/SM4 加解密、HMAC 签名验签、数据脱敏、XSS 过滤 |
| `sloth-boot-common-doc` | Knife4j / OpenAPI 3 自动配置，开箱即用文档 |
| `sloth-boot-common-test` | `SlothSpringBootTest`、`SlothMockMvcTest`、`SlothMapperTest` 测试基类 |

### Starter 能力层

| 模块 | 核心能力 |
|------|---------|
| `starter-web` | 全局异常处理、统一返回包装、参数校验、XSS 过滤 |
| `starter-ai` | Spring AI 多模型接入、流式响应（SSE）、Function Calling |
| `starter-redis` | 缓存工具、分布式锁、滑动窗口限流、布隆过滤器、延迟队列 |
| `starter-mybatis` | MyBatis-Plus 增强、自动填充、数据权限、慢 SQL 监控 |
| `starter-auth` | Sa-Token 认证授权、登录/登出、权限注解、UserContext 集成 |
| `starter-thread-pool` | 动态线程池、TTL 上下文透传、虚拟线程、Actuator 监控 |
| `starter-gateway` | 全局过滤器、鉴权、动态路由、WebFlux 异常处理 |
| `starter-monitor` | 健康检查、钉钉/微信告警、JVM 指标、Micrometer Tracing |
| `starter-feign` | 请求头透传、统一错误解码、Fallback 模板 |
| `starter-mq` | RocketMQ 生产消费封装、消息重试、死信队列 |
| `starter-oss` | 统一 OSS 门面（本地 / MinIO / 阿里云） |
| `starter-excel` | EasyExcel 封装、导入校验、流式导出 |
| `starter-job` | XXL-Job 自动注册、任务基类、分片处理 |
| `starter-sentinel` | Sentinel 限流降级、Nacos 动态规则源 |
| `starter-seata` | Seata AT 模式分布式事务 |
| `starter-es` | Elasticsearch 索引管理、CRUD、聚合查询 |
| `starter-sms` | 统一短信门面（阿里云 / 腾讯云） |
| `starter-idempotent` | `@Idempotent` 注解驱动，Token 模式 + Redis 去重 |
| `generator` | MyBatis-Plus 代码生成器，一键生成 CRUD 全套代码 |

### 工具层

| 模块 | 说明 |
|------|------|
| `sloth-boot-generator` | 代码生成器，基于 MyBatis-Plus Generator |
| `sloth-boot-ui` | Vue 3 前端展示应用，包含落地页、6 个在线演示、文档中心 |

## 核心特性

### 统一返回体

所有接口统一使用 `R<T>` 包装响应：

```java
// 成功
R.ok(data)                // { "code": 0, "msg": "操作成功", "data": ... }

// 失败
R.fail("参数错误")         // { "code": 500, "msg": "参数错误", "data": null }
R.fail(GlobalErrorCode.NOT_FOUND)  // { "code": 404, "msg": "资源不存在", ... }
```

### 全局异常处理

`GlobalExceptionHandler` 自动捕获并转换异常：

| 异常类型 | HTTP 状态码 | 说明 |
|---------|-----------|------|
| `BizException` | 对应错误码 | 业务异常，携带错误码和消息 |
| `SystemException` | 500 | 系统异常，记录详细堆栈 |
| `MissingServletRequestParameterException` | 400 | 缺少请求参数 |
| `HttpRequestMethodNotSupportedException` | 405 | 请求方法不支持 |
| `NoHandlerFoundException` | 404 | 资源不存在 |
| `MaxUploadSizeExceededException` | 400 | 上传文件过大 |

### i18n 国际化

所有用户可见的消息均通过 `I18nUtil.getMessage()` 获取，支持中英文切换：

```yaml
# application.yml
spring:
  messages:
    basename: i18n/messages
    encoding: UTF-8
```

### 自动装配

基于 Spring Boot 3.x 的 `AutoConfiguration.imports` 机制：

- 每个 starter 都有独立的自动配置类
- 使用 `@ConditionalOnMissingBean` 允许业务侧覆盖默认实现
- 使用 `@ConditionalOnProperty` 支持功能开关
- 使用 `@ConditionalOnClass` 实现优雅的依赖守卫

## 配置约定

所有模块统一使用 `sloth.*` 前缀：

```yaml
sloth:
  web:
    unified-response: true
    unified-exception: true
  redis:
    enabled: true
    mode: single
    address: 127.0.0.1:6379
  auth:
    enabled: true
    token-name: Authorization
  thread-pool:
    enabled: true
    core-size: 8
    max-size: 32
  monitor:
    enabled: true
    alarm:
      enabled: false
```

## 项目结构

```
sloth-boot/
├── sloth-boot-common/           # 基础公共层（5 个模块）
├── sloth-boot-starter/          # 能力 Starter 层（19 个模块）
├── sloth-boot-generator/        # 代码生成器
├── sloth-boot-example/          # 示例工程
├── sloth-boot-ui/               # Vue 3 前端展示应用
├── docs/                        # 项目文档
├── CHANGELOG.md                 # 更新日志
└── README.md                    # 项目说明
```

## 在线演示

Sloth Boot 提供 6 个交互式在线演示，覆盖企业开发高频场景：

| 演示 | 说明 |
|------|------|
| 系统管理 | Sa-Token 认证授权、RBAC 权限控制、数据脱敏 |
| 商品管理 | BloomFilter 缓存穿透防护、分布式锁、逻辑过期 |
| 订单管理 | @Idempotent 幂等、@RateLimit 限流、延迟队列 |
| AI 助手 | 同步/SSE 流式对话、多轮会话、结构化输出 |
| 安全工具 | AES/RSA/SM4 加解密、BCrypt 哈希、XSS 防护 |
| 系统监控 | JVM 指标、线程池动态管理、Micrometer 计数器 |

> 在线地址：[https://guohuaijian.github.io/SlothBoot/](https://guohuaijian.github.io/SlothBoot/)

## 相关链接

- GitHub: [https://github.com/GuoHuaijian/SlothBoot](https://github.com/GuoHuaijian/SlothBoot)
- 问题反馈: [GitHub Issues](https://github.com/GuoHuaijian/SlothBoot/issues)
- 讨论交流: [GitHub Discussions](https://github.com/GuoHuaijian/SlothBoot/discussions)
