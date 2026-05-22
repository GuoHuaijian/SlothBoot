# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/),
and this project adheres to [Semantic Versioning](https://semver.org/).

## [Unreleased]

### Added
- `sloth-boot-ui`: Vue 3 + TypeScript + Vite 前端展示应用（落地页、6 个在线演示、文档中心、模块浏览器）
- 5 个 common 模块独立 README（common-core、common-log、common-security、common-doc、common-test）
- 18 个 starter 模块独立 README（每个模块含配置项、使用示例、FAQ）
- FAQ 常见问题文档（`docs/faq.md`）
- 测试指南文档（`docs/testing-guide.md`）
- 迁移指南文档（`docs/migration-guide.md`）
- GitHub 社区文件完善（Issue 模板、PR 模板、CODEOWNERS、FUNDING.yml、dependabot.yml）
- 前端文档中心新增：快速开始、常见问题、测试指南、更新日志页面
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
- 移除 `EncryptTypeHandler` 中硬编码的 AES 密钥，改为必须配置 `sloth.mybatis.encrypt-key`
- `DataPermissionInterceptor` 和 `DataScopeInterceptor` SQL 注入防护（字符串值转义）
- `GlobalExceptionHandler` 中字符串匹配改为 `Class.isAssignableFrom`，提高健壮性
- `BaseQuery.pageNum/pageSize` 从 `Integer` 改为 `int`，防止拆箱 NPE
- `JsonTypeHandler` 反序列化路径修复（读取时正确解析 JSON）
- `AuthAutoConfiguration` 移除 `@ComponentScan`，改为显式 `@Bean` 注册
- `ThreadPoolAlarmTask` 注册为 Bean，修复队列容量计算公式
- `DocProperties` 和 GitHub Issue 模板中的占位符 URL 修正为真实地址
- `GlobalErrorCode` 新增 `UNSUPPORTED_MEDIA_TYPE(415)` 错误码
- i18n：`R.java` 和 `GlobalExceptionHandler` 中硬编码中文改为消息键
- `PageResult` 移除从 `R.java` 误拷贝的 `isSuccess()` 死方法
- `RedisIdGenerator` Lua 脚本缓存优化（不再每次调用重新解析）
- `RedisDelayQueue` 实现 `DisposableBean`，应用关闭时优雅关闭消费者线程池
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
- `ThreadPoolSnapshot` 从普通类改为 Java 21 record 类型
- i18n：所有中文错误消息改为国际化消息键（支持中英文切换）
- `TraceContext` 和 `UserContext` 内部类移除冗余手写 getter（`@Data` 已自动生成）
- CI 流水线移除 `-DskipTests`，运行完整测试
- 示例服务 `allow-bean-definition-overriding` 改为 `false`
- 移除废弃的 `bootstrap.yml`，使用 `spring.config.import` 方式
- 示例服务添加优雅停机配置（`server.shutdown=graceful`）
- 消除 14 个重复文件（11 个 Jackson 序列化器 + 3 个 Feign 异常类）
- CI 流水线新增 Checkstyle、SpotBugs、Spotless 检查
- 升级依赖版本：Spring Cloud `2025.0.0` → `2025.1.1`、Spring Cloud Alibaba `2025.0.0.0` → `2025.1.0.0`
- 升级依赖版本：Spring AI `1.1.4` → `1.1.6`、EasyExcel `3.3.4` → `4.0.3`、MapStruct `1.5.5.Final` → `1.6.3`
- 升级依赖版本：Sa-Token `1.37.0` → `1.45.0`、Ip2region `2.7.0` → `3.3.7`、Mica XSS `3.1.6` → `4.0.2`
- 升级 Maven 插件：`maven-compiler-plugin` `3.11.0` → `3.15.0`、`native-maven-plugin` `0.10.4` → `1.1.0`
- 升级 CI：`actions/checkout` v4 → v6、`actions/setup-java` v4 → v5、`actions/upload-artifact` v4 → v7

## [1.0.0-SNAPSHOT] - 2026-04-23

### Added
- 初始项目骨架搭建
- 22 个 starter 模块
- 5 个 common 模块
- 示例工程
- GitHub 社区文件（CONTRIBUTING、CODE_OF_CONDUCT、SECURITY、SUPPORT）
- CI/CD 流水线（GitHub Actions）
