<div align="center">

# 🦥 Sloth Boot

> **慢工出细活。** 一个面向个人开源与中小团队的 Spring Boot 多模块基础脚手架。  
> 不造平台，只沉淀高频、可复用的工程化基础能力。

<br/>

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-6DB33F?style=flat-square&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.0.0-0A6CFF?style=flat-square&logo=spring&logoColor=white)](https://spring.io/projects/spring-cloud)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-1.0.0-FF6F00?style=flat-square&logo=spring&logoColor=white)](https://spring.io/projects/spring-ai)
[![Maven](https://img.shields.io/badge/Maven-3.9+-C71A36?style=flat-square&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue?style=flat-square&logo=apache&logoColor=white)](./LICENSE)
[![Build](https://img.shields.io/github/actions/workflow/status/GuoHuaijian/slothboot/ci.yml?style=flat-square&logo=githubactions&logoColor=white&label=CI)](./github/workflows/ci.yml)
[![Stars](https://img.shields.io/github/stars/GuoHuaijian/slothboot?style=flat-square&logo=github&logoColor=white)](https://github.com/GuoHuaijian/slothboot/stargazers)
[![Issues](https://img.shields.io/github/issues/GuoHuaijian/slothboot?style=flat-square&logo=github)](https://github.com/GuoHuaijian/slothboot/issues)
[![PRs Welcome](https://img.shields.io/badge/PRs-Welcome-brightgreen?style=flat-square&logo=git&logoColor=white)](./CONTRIBUTING.md)
[![Status](https://img.shields.io/badge/Status-Active-success?style=flat-square)](./CHANGELOG.md)

<br/>

[📖 快速开始](#-快速开始) · [🧩 模块导航](#-模块导航) · [🗺 Roadmap](#-roadmap) · [🤝 参与贡献](./CONTRIBUTING.md) · [📋 更新日志](./CHANGELOG.md)

</div>

---

## 🦥 为什么叫 Sloth Boot？

树懒不慌不忙，但每一步都踩得很稳。  
这个项目的理念相同 —— **不追求大而全，只做扎实可用的基础层**：

- ✅ 结构清晰，可以按模块裁剪引入
- ✅ 配置收敛，统一 `sloth.*` 前缀，一目了然
- ✅ 自动装配，业务侧接入几乎零配置
- ✅ 适合长期维护，而不是一次性造轮子
- ✅ 代码可读，注释友好，适合学习和二次定制

---

## ✨ 核心特性

<table>
<tr>
<td width="50%">

**🏗 工程化结构**
- Maven 多模块，按需引入
- `common` 基础层 + `starter` 能力层分离
- `example` 示例工程，可直接运行对照

</td>
<td width="50%">

**⚙️ 统一配置治理**
- 所有自定义配置统一 `sloth.*` 前缀
- `@ConditionalOnMissingBean` 支持业务侧覆盖
- 通过 `AutoConfiguration.imports` 自动装配

</td>
</tr>
<tr>
<td width="50%">

**🔧 覆盖高频场景**
- Web / Redis / MQ / MyBatis / 线程池
- 网关 / Feign / Sentinel / Seata
- OSS / Excel / 短信 / 任务调度 / ES / AI 集成

</td>
<td width="50%">

**🧪 测试友好**
- 提供 Spring Boot / MockMvc / Mapper 测试基类
- 集成 CI/CD，自动构建验证
- 示例工程配套 Knife4j 文档

</td>
</tr>
</table>

---

## 🏛 技术栈

| 层次 | 技术选型 |
|------|---------|
| **语言 & 运行时** | Java 21 · Maven 3.9+ |
| **核心框架** | Spring Boot 3.5.0 · Spring Cloud 2025.0.0 |
| **微服务生态** | Spring Cloud Alibaba 2025.0.0.0 · Nacos · Sentinel · Seata |
| **AI 能力** | Spring AI 1.0.0 · OpenAI · 通义千问 · DeepSeek · Ollama |
| **数据层** | MyBatis-Plus · MySQL · Elasticsearch |
| **缓存 & 消息** | Redis · RocketMQ |
| **任务 & 文件** | XXL-Job · MinIO · 阿里云 OSS |
| **网关 & RPC** | Spring Cloud Gateway · OpenFeign |
| **文档 & 监控** | Knife4j · Spring Actuator · Prometheus |

---

## 🗂 模块结构

```text
sloth-boot/
│
├── 📦 sloth-boot-common/            # 基础公共层
│   ├── sloth-boot-common-core       # 常量 / 异常 / 返回体 / 上下文 / 工具类 / 注解
│   ├── sloth-boot-common-log        # Trace 过滤 / 请求日志 / 操作日志切面
│   ├── sloth-boot-common-security   # 加解密 / 签名 / 脱敏 / XSS 处理
│   ├── sloth-boot-common-doc        # OpenAPI / Knife4j 文档自动配置
│   └── sloth-boot-common-test       # 测试基类
│
├── 🚀 sloth-boot-starter/           # 能力 Starter 层
│   ├── sloth-boot-starter-web       # Web 基础 / 统一异常 / 统一返回 / 参数校验
│   ├── sloth-boot-starter-ai        # Spring AI 集成 / ChatClient 封装 / OpenAI 模型接入
│   ├── sloth-boot-starter-redis     # RedisTemplate / 缓存工具 / 分布式锁 / 限流
│   ├── sloth-boot-starter-mq        # RocketMQ 生产消费封装
│   ├── sloth-boot-starter-mybatis   # MyBatis-Plus 插件 / 自动填充 / TypeHandler
│   ├── sloth-boot-starter-thread-pool  # 线程池 / 上下文透传 / 监控端点
│   ├── sloth-boot-starter-sentinel  # Sentinel 限流降级 / Nacos 规则源
│   ├── sloth-boot-starter-monitor   # 健康检查 / 告警 / Actuator 增强
│   ├── sloth-boot-starter-feign     # Feign 拦截器 / 解码器 / Fallback 模板
│   ├── sloth-boot-starter-gateway   # Gateway 过滤器 / 异常处理 / 动态路由
│   ├── sloth-boot-starter-oss       # 本地 / MinIO / 阿里云 OSS 统一封装
│   ├── sloth-boot-starter-excel     # Excel 导入导出与响应封装
│   ├── sloth-boot-starter-job       # XXL-Job 自动配置与任务基类
│   ├── sloth-boot-starter-seata     # Seata AT 模式自动配置
│   ├── sloth-boot-starter-es        # Elasticsearch 常用操作封装
│   ├── sloth-boot-starter-sms       # 阿里云 / 腾讯云短信门面
│   └── sloth-boot-starter-idempotent  # 幂等增强 / Token 模式
│
├── 🧪 sloth-boot-example/           # 示例工程
│   └── sloth-boot-example-service   # 单体服务示例，本地可直接运行
│
├── 📄 README.md
├── 📄 CHANGELOG.md
├── 📄 LICENSE
└── 📄 .gitignore
```

---

## 🧩 模块导航

### Common 基础层

| 模块 | 核心能力 | 推荐先看 |
|------|---------|:-------:|
| `common-core` | 基础常量、统一返回体 `R<T>`、业务异常体系、请求上下文、工具类、通用注解 | ✅ |
| `common-log` | TraceId 过滤器、HTTP 请求日志、`@OperationLog` 操作日志切面 | ✅ |
| `common-security` | AES/RSA 加解密、HMAC 签名验签、数据脱敏、XSS 过滤 | — |
| `common-doc` | Knife4j / OpenAPI 3 自动配置，开箱即用文档 | — |
| `common-test` | `SlothSpringBootTest`、`SlothMockMvcTest`、`SlothMapperTest` 测试基类 | — |

### Starter 能力层

| 模块 | 核心能力 | 状态 |
|------|---------|------|
| `starter-ai` | Spring AI ChatClient 封装、默认 Prompt 配置、OpenAI 模型接入 | ✅ 新增 |
| `starter-web` | 全局异常处理、统一返回包装、参数校验增强、Jackson 配置 | ✅ 骨架完成 |
| `starter-redis` | RedisTemplate 增强、缓存工具类、Redisson 分布式锁、滑动窗口限流 | ✅ 骨架完成 |
| `starter-mq` | RocketMQ 生产者/消费者基础封装、消息重试、死信队列处理 | ✅ 骨架完成 |
| `starter-mybatis` | MyBatis-Plus 配置、逻辑删除、自动填充、枚举 TypeHandler | ✅ 骨架完成 |
| `starter-thread-pool` | 动态线程池、TTL 上下文透传、Actuator 监控端点 | ✅ 骨架完成 |
| `starter-sentinel` | Sentinel 规则配置、Nacos 动态数据源、降级处理 | ✅ 骨架完成 |
| `starter-monitor` | 自定义健康指标、告警通知、Actuator 端点增强 | ✅ 骨架完成 |
| `starter-feign` | Feign 请求头透传、统一错误解码、Fallback 基类 | ✅ 骨架完成 |
| `starter-gateway` | 全局过滤器、鉴权、WebFlux 异常处理、动态路由 | ✅ 骨架完成 |
| `starter-oss` | 统一 OSS 门面，支持本地 / MinIO / 阿里云 OSS | ✅ 骨架完成 |
| `starter-excel` | EasyExcel 封装，支持导入校验、流式导出、HTTP 直接响应 | ✅ 骨架完成 |
| `starter-job` | XXL-Job 自动注册、任务基类、分片处理模板 | ✅ 骨架完成 |
| `starter-seata` | Seata AT 模式自动配置，支持 Nacos 注册 | ✅ 骨架完成 |
| `starter-es` | Elasticsearch 索引管理、CRUD 封装、聚合查询工具 | ✅ 骨架完成 |
| `starter-sms` | 统一短信门面，支持阿里云 / 腾讯云，模板抽象 | ✅ 骨架完成 |
| `starter-idempotent` | `@Idempotent` 注解驱动，Token 模式 + Redis 去重 | ✅ 骨架完成 |

---

## 🚀 快速开始

### 环境要求

| 工具 | 版本要求 |
|------|---------|
| JDK | 21+ |
| Maven | 3.9+ |
| MySQL | 8.0+（示例工程需要） |
| Redis | 6.0+（示例工程需要） |

### 1. 克隆仓库

```bash
git clone https://github.com/your-org/sloth-boot.git
cd sloth-boot
```

### 2. 构建全量模块

```bash
mvn clean install -DskipTests
```

### 3. 启动示例工程

```bash
# 修改 example 配置文件中的 MySQL / Redis 地址
vim sloth-boot-example/sloth-boot-example-service/src/main/resources/application.yml

# 启动
mvn -pl sloth-boot-example/sloth-boot-example-service spring-boot:run
```

### 4. 验证运行

```bash
# 健康检查
curl http://localhost:8080/actuator/health

# API 文档
open http://localhost:8080/doc.html
```

> 💡 **推荐阅读顺序：**  
> `common-core` → `starter-web` → `starter-redis` → `starter-mybatis` → `example-service`

---

## ⚙️ 配置约定

```yaml
sloth:
  web:
    unified-response: true        # 开启统一返回包装
    unified-exception: true       # 开启全局异常处理

  ai:
    enabled: false
    model: gpt-4o-mini
    default-system-prompt: "你是 Sloth Boot 内置 AI 助手"

spring:
  ai:
    model:
      chat: openai
    openai:
      base-url: https://api.openai.com
      api-key: ${OPENAI_API_KEY:}

  redis:
    lock:
      prefix: "sloth:lock:"       # 分布式锁 Key 前缀
      expire: 30                  # 默认锁超时（秒）

  thread-pool:
    enabled: true
    core-size: 8
    max-size: 32
    queue-capacity: 500

  idempotent:
    enabled: true
    expire: 60                    # Token 有效期（秒）
```

> 所有模块统一使用 `sloth.*` 前缀，避免配置散落在多个命名空间。  
> 每个 starter 均支持通过 `@ConditionalOnMissingBean` 覆盖默认 Bean。

---

## 🗺 Roadmap

```text
Phase 1 — 骨架完成               ████████████████████  100% ✅
Phase 2 — 编译修复与验证          ████░░░░░░░░░░░░░░░░   20% 🔧
Phase 3 — 文档 & 单测补全         ░░░░░░░░░░░░░░░░░░░░    0% 📋
Phase 4 — Release 流程与版本策略   ░░░░░░░░░░░░░░░░░░░░    0% 🏷
Phase 5 — 架构图 & 可视化文档      ░░░░░░░░░░░░░░░░░░░░    0% 🎨
```

- [x] 完成基础多模块 Maven 结构
- [x] 完成 `common` 基础层骨架
- [x] 完成主干 `starter` 模块骨架
- [x] 完成示例工程与基础仓库文档
- [x] 完成 GitHub 协作模板和基础 CI
- [ ] 完成一轮 `mvn clean verify` 编译修复
- [ ] 补充 `docs/` 模块级别文档目录
- [ ] 增加单元测试与集成测试覆盖
- [ ] 建立 Release 版本策略与 Changelog 自动生成
- [ ] 补充架构图、模块依赖图与接入示意图

---

## 🤝 参与贡献

欢迎任何形式的贡献，无论是 Issue、PR 还是文档改进。

```bash
# Fork 本仓库后
git checkout -b feat/your-feature
git commit -m "feat: add your feature"
git push origin feat/your-feature
# 提交 Pull Request
```

请在贡献前阅读：

- [🤝 CONTRIBUTING.md](./CONTRIBUTING.md) — 贡献指南
- [📜 CODE_OF_CONDUCT.md](./CODE_OF_CONDUCT.md) — 行为准则
- [🔒 SECURITY.md](./SECURITY.md) — 安全漏洞报告
- [💬 SUPPORT.md](./SUPPORT.md) — 获取帮助

---

## 📄 License

```
Copyright 2024 Sloth Boot Contributors

Licensed under the Apache License, Version 2.0
http://www.apache.org/licenses/LICENSE-2.0
```

---

<div align="center">

如果这个项目对你有帮助，欢迎点一个 ⭐ Star，这是对维护者最大的鼓励。

</div>
