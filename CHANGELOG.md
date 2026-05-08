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
- Spotless/Checkstyle/SpotBugs 静态分析工具链
- Redis Bloom 过滤器支持（starter-redis）
- Redis Pub/Sub 模板（starter-redis）
- Gzip 响应压缩（starter-web）
- PermissionService RBAC SPI（starter-auth）
- Token 黑名单服务（starter-auth）
- AI 结构化输出支持（starter-ai）
- AI Function Calling 注册器（starter-ai）
- 死信队列处理器（starter-mq）
- 预签名 URL 生成（starter-oss）
- 动态线程池重配置（starter-thread-pool）
- Gateway 重试过滤器（starter-gateway）
- 可配置的监控告警阈值（starter-monitor）
- 错误码注册表（ErrorRegistry）
- 配置参考文档（`docs/configuration-reference.md`）
- 19 个关键包的 `package-info.java`
- 全模块 Javadoc 改进
- 7 个 AutoConfiguration 条件装配测试（mybatis、auth、thread-pool、feign、monitor、log、security）

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
- RateLimiterAspect RedisScript 性能问题（原先每次请求重建）
- MybatisPlusAutoConfiguration 硬编码 DbType.MYSQL（改为自动检测）
- RedisIdGenerator 线程安全问题（原子 INCR+EXPIRE Lua 脚本）
- WebAutoConfiguration 异常吞没问题
- RedissonDistributedLock 解锁安全性

### Changed
- CI 流水线移除 `-DskipTests`，运行完整测试
- 示例服务 `allow-bean-definition-overriding` 改为 `false`
- 移除废弃的 `bootstrap.yml`，使用 `spring.config.import` 方式
- 示例服务添加优雅停机配置（`server.shutdown=graceful`）
- 消除 14 个重复文件（11 个 Jackson 序列化器 + 3 个 Feign 异常类）
- CI 流水线新增 Checkstyle、SpotBugs、Spotless 检查

## [1.0.0-SNAPSHOT] - 2026-04-23

### Added
- 初始项目骨架搭建
- 22 个 starter 模块
- 5 个 common 模块
- 示例工程
- GitHub 社区文件（CONTRIBUTING、CODE_OF_CONDUCT、SECURITY、SUPPORT）
- CI/CD 流水线（GitHub Actions）
