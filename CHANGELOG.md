# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/),
and this project adheres to [Semantic Versioning](https://semver.org/).

## [Unreleased]

### Added
- `sloth-boot-starter-auth`: Sa-Token 认证授权模块，支持登录/登出/Token 校验/权限拦截
- `sloth-boot-generator`: 代码生成器，基于 MyBatis-Plus Generator，支持 Entity/Mapper/Service/Controller 一键生成
- AI 流式响应支持（SSE），`AiChatClient` 新增 `chatStream()` 方法
- Micrometer Tracing 分布式追踪桥接（`TraceContextBridge`）
- GraalVM Native Image 支持（`native-maven-plugin` + `reflect-config.json`）
- Java 21 虚拟线程支持（`sloth.thread-pool.virtual-enabled=true`）
- i18n 国际化支持（中英文消息文件 + `I18nUtil` 工具类）
- Spring Boot 配置元数据（`additional-spring-configuration-metadata.json`），支持 IDE 自动补全
- 53 个单元测试（common-core、starter-web、starter-redis）
- Docker 支持（Dockerfile + docker-compose.yml）
- 示例控制器：Redis 缓存/分布式锁/限流/幂等、Sa-Token 认证、Excel 导入导出
- DataScopeInterceptor 真正的 WHERE 子句注入（支持 dept/dept_and_below/self 三种数据范围）

### Fixed
- `UserContext` 和 `TraceContext` 改为 `TransmittableThreadLocal`，修复异步上下文丢失
- 移除 `HeaderConstant` 中硬编码的内部调用密钥
- `BaseEntity` 从 common-core 移至 starter-mybatis，common-core 保持纯 POJO
- 移除 starter-redis 中重复的 `RedisHealthIndicator`（保留 starter-monitor 版本）
- `GatewayAutoConfiguration` 中 `SentinelFallbackHandler` 添加 `@ConditionalOnClass` 条件守卫
- MQ starter 依赖从 `sloth-boot-starter-redis` 改为 `spring-boot-starter-data-redis`
- README 配置示例前缀错误修正
- POM SCM URL 和开发者信息更新为真实 GitHub 地址
- CORS 配置修正（credentials=true 时不能用 `*`）
- `.gitignore` 添加 `.m2/`、`*.jar`、`.env` 等条目

### Changed
- CI 流水线移除 `-DskipTests`，运行完整测试
- 示例服务 `allow-bean-definition-overriding` 改为 `false`
- 移除废弃的 `bootstrap.yml`，使用 `spring.config.import` 方式
- 示例服务添加优雅停机配置（`server.shutdown=graceful`）

## [1.0.0-SNAPSHOT] - 2026-04-23

### Added
- 初始项目骨架搭建
- 22 个 starter 模块
- 5 个 common 模块
- 示例工程
- GitHub 社区文件（CONTRIBUTING、CODE_OF_CONDUCT、SECURITY、SUPPORT）
- CI/CD 流水线（GitHub Actions）
