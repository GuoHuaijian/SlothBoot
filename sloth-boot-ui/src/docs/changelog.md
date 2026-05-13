# 更新日志

本文档记录 Sloth Boot 的重要变更。完整变更历史请查看 [CHANGELOG.md](https://github.com/GuoHuaijian/SlothBoot/blob/main/CHANGELOG.md)。

---

## 未发布 (Unreleased)

### 新增

- **前端展示应用**：Vue 3 + TypeScript + Vite 前端展示应用，包含落地页、6 个在线演示、3 个文档页面、模块浏览器
- **GitHub Pages 部署**：自动化部署工作流，前端展示应用可在线访问
- **全模块 README**：5 个 common 模块 + 18 个 starter 模块均配有独立 README（配置项、使用示例、FAQ）
- **文档完善**：FAQ 常见问题、测试指南、迁移指南、配置参考文档
- **社区文件**：Issue 模板、PR 模板、CODEOWNERS、FUNDING.yml、dependabot.yml
- **AI 能力**：SSE 流式响应、Function Calling、结构化输出
- **监控增强**：Micrometer Tracing 分布式追踪、GraalVM Native Image 支持
- **线程池**：Java 21 虚拟线程支持、动态重配置、Actuator 监控端点
- **安全**：Bloom 过滤器、Token 黑名单、PermissionService RBAC SPI
- **消息队列**：死信队列处理器
- **对象存储**：预签名 URL 生成
- **网关**：重试过滤器

### 修复

- **安全修复**：移除 `EncryptTypeHandler` 中硬编码的 AES 密钥，改为配置注入
- **安全修复**：`DataPermissionInterceptor` 和 `DataScopeInterceptor` SQL 注入防护（字符串值转义）
- `GlobalExceptionHandler` 中字符串匹配改为 `Class.isAssignableFrom`，提高健壮性
- `BaseQuery.pageNum/pageSize` 从 `Integer` 改为 `int`，防止拆箱 NPE
- `JsonTypeHandler` 反序列化路径修复（读取时正确解析 JSON）
- `AuthAutoConfiguration` 移除 `@ComponentScan`，改为显式 `@Bean` 注册
- `ThreadPoolAlarmTask` 注册为 Bean，修复队列容量计算公式
- `DocProperties` 和 GitHub Issue 模板中的占位符 URL 修正
- `GlobalErrorCode` 新增 `UNSUPPORTED_MEDIA_TYPE(415)` 错误码
- i18n：`R.java` 和 `GlobalExceptionHandler` 中硬编码中文改为消息键

### 变更

- `ThreadPoolSnapshot` 从普通类改为 Java 21 record 类型
- i18n：所有中文错误消息改为国际化消息键（支持中英文切换）
- CI 流水线移除 `-DskipTests`，运行完整测试
- 消除 14 个重复文件（11 个 Jackson 序列化器 + 3 个 Feign 异常类）

---

## 1.0.0-SNAPSHOT (2026-04-23)

### 新增

- 初始项目骨架搭建
- 22 个 starter 模块
- 5 个 common 模块
- 示例工程
- GitHub 社区文件（CONTRIBUTING、CODE_OF_CONDUCT、SECURITY、SUPPORT）
- CI/CD 流水线（GitHub Actions）

---

> 查看完整变更历史：[GitHub CHANGELOG](https://github.com/GuoHuaijian/SlothBoot/blob/main/CHANGELOG.md)
