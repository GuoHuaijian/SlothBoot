# SlothBoot 模块规范与边界整改设计

> 日期：2026-05-30
> 状态：待审批
> 范围：项目规范整理、模块边界修正、命名一致性、依赖管理优化

## 1. 背景与目标

SlothBoot 是一个企业级 Spring Cloud Alibaba 脚手架项目（31 个 Maven 模块）。随着模块增多，出现了以下问题：

- **模块边界模糊**：Servlet 代码侵入 common-core、业务常量混入框架层、HealthIndicator 错放
- **命名不一致**：缩写 vs 全称、子包名混乱（health/monitor/monitoring）
- **依赖管理粗糙**：parent POM 把大量 heavy dependency 传递给所有模块
- **重复代码**：RocketMQ HealthIndicator 有两份实现

**目标**：建立清晰的模块规范，完成代码整改，使项目达到开源脚手架项目的质量标准。

## 2. 模块边界规则

### 2.1 sloth-boot-dependencies
**职责**：BOM 版本目录，只声明版本号，不含代码。

### 2.2 sloth-boot-parent
**职责**：共享构建配置和最少的全局依赖。

**允许的依赖**（仅这些）：
- `lombok`（编译期，所有模块都需要）
- `jakarta.validation-api`（provided，注解级）
- `slf4j-api`（日志门面）
- 测试依赖：`spring-boot-starter-test`、`mockito`、`assertj`

**不允许的依赖**（下沉到实际使用的模块）：
- `hutool-core` → `common-core`
- `guava` → `common-core`
- `jackson-databind/core/annotations` → `common-core`
- `mapstruct` + `mapstruct-processor` → `common-core`
- `transmittable-thread-local` → `common-core`（或 `starter-thread-pool`）
- `jakarta.servlet-api` → `starter-web`

### 2.3 sloth-boot-common-core
**职责**：框架级基础设施，不依赖任何具体技术栈。

**可以放**：
- 统一响应：`R<T>`、`PageResult`
- 异常体系：`BaseException`、`BizException`、`ErrorCode`、`GlobalErrorCode`
- 基础模型：`BaseEntity`、`BaseDTO`、`BaseVO`、`BaseQuery`、`TreeNode`
- 枚举：`DeletedEnum`、`StatusEnum`、`YesNoEnum`、`IBaseEnum`
- 框架级常量：`CommonConstant`、`HeaderConstant`、`HttpStatus`
- 上下文：`TraceContext`、`UserContext`
- 事件机制：`BaseEvent`、`EventPublisher`、`AbstractEventListener`
- 生命周期：`Lifecycle`、`AbstractLifecycle`
- 装饰器：`AbstractDecorator`
- 拦截器：`AbstractMethodInterceptor`（非 Servlet 的方法级拦截）
- 通用工具类：`AssertUtil`、`CollectionUtil`、`DateUtil`、`JsonUtil`、`StringUtil`、`TemplateUtil` 等

**不可以放**：
- ~~Servlet/Web 代码~~（`ServletUtil`、`IpUtil`、`AbstractHandlerInterceptor` → 移到 `starter-web`）
- ~~安全/加密代码~~（`DesensitizeUtil` → 移到 `common-security`）
- ~~业务域常量~~（`USER_CACHE_PREFIX`、`ROLE_CACHE_PREFIX` 等 → 移除）
- ~~具体技术栈代码~~（Redis、MQ、MyBatis 等 → 各自 starter）

### 2.4 sloth-boot-common-{name}
**职责**：某个技术领域的通用能力（非自动配置，无 AutoConfiguration 类）。

**命名规则**：`sloth-boot-common-{capability}`

**示例**：
- `common-log`：日志相关（TraceId、操作日志注解/切面）
- `common-security`：安全相关（加密、脱敏、XSS、签名）
- `common-doc`：API 文档（Knife4j/Swagger）
- `common-test`：测试支持

**注意**：common 模块不包含 AutoConfiguration，那是 starter 的职责。

### 2.5 sloth-boot-starter-{name}
**职责**：某个技术领域的 Spring Boot 自动配置。

**规则**：
1. 每个 starter 只包含自己领域的代码，不越界
2. HealthIndicator 放在自己的 starter 中，不放在 `starter-monitor`
3. 每个 starter 有且仅有一个 `{Name}AutoConfiguration`

### 2.6 sloth-boot-starter-monitor
**职责**：通用监控基础设施（聚合健康端点、告警框架、Actuator 配置）。

**可以放**：聚合 dashboard、告警服务、通用健康聚合端点
**不可以放**：具体中间件的 HealthIndicator（各自回各自的 starter）

## 3. 命名规范

### 3.1 Artifact ID

格式：`sloth-boot-{category}-{capability}`

| 类别 | 格式 | 示例 |
|------|------|------|
| 核心模块 | `sloth-boot-dependencies`、`sloth-boot-parent` | — |
| 通用模块 | `sloth-boot-common-{capability}` | `sloth-boot-common-core` |
| 启动器 | `sloth-boot-starter-{capability}` | `sloth-boot-starter-redis` |
| 示例 | `sloth-boot-example-{name}` | `sloth-boot-example-service` |
| 工具 | `sloth-boot-{tool}` | `sloth-boot-generator` |

**禁止缩写**：capability 必须使用完整单词。
- ~~`mq`~~ → `rocketmq`（因为它只支持 RocketMQ）
- ~~`es`~~ 保持不变（Elasticsearch 的通用简称，业界共识）

### 3.2 Java 包名

格式与 artifact suffix 完全一致：

| Artifact Suffix | Java 包名 | 说明 |
|-----------------|-----------|------|
| `common-core` | `com.sloth.boot.common` | common-core 的特殊约定 |
| `common-log` | `com.sloth.boot.common.log` | — |
| `starter-redis` | `com.sloth.boot.starter.redis` | — |
| `starter-thread-pool` | `com.sloth.boot.starter.threadpool` | 修正：补上 `pool` |
| `starter-rocketmq` | `com.sloth.boot.starter.rocketmq` | 修正：从 `mq` 改为 `rocketmq` |

### 3.3 类名规范

| 类型 | 命名格式 | 示例 |
|------|---------|------|
| 自动配置 | `{Capability}AutoConfiguration` | `RedisAutoConfiguration`、`MybatisAutoConfiguration` |
| 属性类 | `{Capability}Properties` | `RedisProperties`、`MQProperties` |
| 健康检查 | `{Capability}HealthIndicator` | `RedisHealthIndicator` |
| 指标 | `{Capability}Metrics` / `{Capability}MetricsAutoConfiguration` | — |

**禁止在 AutoConfiguration 类名中包含厂商名**：
- ~~`MybatisPlusAutoConfiguration`~~ → `MybatisAutoConfiguration`

### 3.4 子包名规范

统一使用以下子包名：

| 用途 | 包名 | 禁止使用 |
|------|------|---------|
| 健康检查 | `health` | ~~`monitor`~~、~~`monitoring`~~ |
| 指标采集 | `metrics` | ~~`monitor`~~ |
| 自动配置 | `config` | — |
| 核心实现 | `core` | — |
| 辅助工具 | `support` | — |

### 3.5 类名中缩写规范

| 缩写 | 全称 | 类名中的写法 |
|------|------|-------------|
| MQ | MessageQueue | `RocketMQ`（与模块名一致） |
| RPC | RemoteProcedureCall | `Rpc`（业界惯例，保持缩写） |
| OSS | ObjectStorage | `Oss`（业界惯例） |
| SMS | ShortMessage | `Sms`（业界惯例） |
| ES | Elasticsearch | `Es`（业界惯例） |
| XSS | CrossSiteScripting | `Xss`（业界惯例） |

## 4. Starter 标准化结构

每个 starter 模块必须遵循以下目录结构：

```
sloth-boot-starter-{name}/
  src/main/java/com/sloth/boot/starter/{name}/
    config/          # AutoConfiguration + Properties（必须有）
    core/            # 核心实现
    health/          # HealthIndicator（如有）
    metrics/          # Metrics 相关（如有）
    support/         # 辅助/工具类
    ...              # starter 特有的业务子包
  src/main/resources/
    META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
    META-INF/additional-spring-configuration-metadata.json（如有自定义配置提示）
  src/test/java/
```

### 4.1 必须有的内容

- `config/{Name}AutoConfiguration`：自动配置类
- `config/{Name}Properties`：配置属性类（`@ConfigurationProperties`）
- `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`：注册自动配置

### 4.2 可选内容

- `health/{Name}HealthIndicator`：健康检查（如 starter 需要）
- `metrics/`：指标采集（如 starter 需要）
- `support/`：辅助工具类

## 5. 依赖管理规范

### 5.1 三层依赖模型

```
sloth-boot-dependencies (BOM: 版本号管理)
  ↓ import
sloth-boot-parent (最少全局依赖 + 构建插件)
  ↓ extends
各 leaf 模块 (按需声明自己的依赖)
```

### 5.2 版本管理

- 所有第三方依赖版本 **只在 `sloth-boot-dependencies` 中声明**
- 内部模块间依赖 **不需要显式 `<version>`**（由 BOM 管理）
- 各 leaf 模块 pom.xml 中删除 `<version>${revision}</version>`

### 5.3 依赖声明原则

1. **parent POM 只放"所有模块都需要"的依赖**
2. **按需声明**：模块真正使用的依赖才声明
3. **optional 标记**：如果依赖是可选功能，标记 `<optional>true</optional>`
4. **provided 标记**：如果依赖由运行时容器提供，标记 `<scope>provided</scope>`

## 6. 代码整改清单

### 6.1 高优先级：架构边界修复

#### 6.1.1 移动 Servlet 代码出 common-core

**涉及文件**：
- `common-core/.../util/ServletUtil.java` → `starter-web/.../util/ServletUtil.java`
- `common-core/.../util/IpUtil.java` → `starter-web/.../util/IpUtil.java`
- `common-core/.../interceptor/AbstractHandlerInterceptor.java` → `starter-web/.../interceptor/AbstractHandlerInterceptor.java`

**包名变更**：`com.sloth.boot.common.util` → `com.sloth.boot.starter.web.util`（ServletUtil、IpUtil）
**包名变更**：`com.sloth.boot.common.interceptor` → `com.sloth.boot.starter.web.interceptor`

**影响范围**：需要更新所有 import 这些类的地方。

#### 6.1.2 移动 DesensitizeUtil 到 common-security

**涉及文件**：
- `common-core/.../util/DesensitizeUtil.java` → `common-security/.../desensitize/DesensitizeUtil.java`

**包名变更**：`com.sloth.boot.common.util.DesensitizeUtil` → `com.sloth.boot.common.security.desensitize.DesensitizeUtil`

**影响范围**：`DesensitizeSerializer` 在 `common-security` 中已引用它，更新 import。

#### 6.1.3 移动 Redis HealthIndicator 到 starter-redis

**涉及文件**：
- `starter-monitor/.../health/RedisHealthIndicator.java` → `starter-redis/.../health/RedisHealthIndicator.java`

**包名变更**：`com.sloth.boot.starter.monitor.health` → `com.sloth.boot.starter.redis.health`

#### 6.1.4 删除 starter-monitor 中的 RocketMQ HealthIndicator

**涉及文件**：
- `starter-monitor/.../health/RocketMQHealthIndicator.java` → 删除

**理由**：`starter-mq` 已有更强版本（含 ServiceState 检查），保留 `starter-mq` 中的版本。

#### 6.1.5 清理 CacheConstant 中的业务域常量

**涉及文件**：
- `common-core/.../constant/CacheConstant.java`

**删除**：`USER_CACHE_PREFIX`、`ROLE_CACHE_PREFIX`、`MENU_CACHE_PREFIX`、`DEPT_CACHE_PREFIX`、`DICT_CACHE_PREFIX`、`CONFIG_CACHE_PREFIX`

**保留**：`DEFAULT_CACHE_PREFIX`（"sloth:"，框架级）

### 6.2 中优先级：命名一致性

#### 6.2.1 thread-pool 包名修正

**变更**：`com.sloth.boot.starter.thread` → `com.sloth.boot.starter.threadpool`

**涉及模块**：`sloth-boot-starter-thread-pool` 全部 Java 文件

#### 6.2.2 mq 模块重命名为 rocketmq

**变更**：
- 目录名：`sloth-boot-starter-mq` → `sloth-boot-starter-rocketmq`
- artifactId：`sloth-boot-starter-mq` → `sloth-boot-starter-rocketmq`
- 包名：`com.sloth.boot.starter.mq` → `com.sloth.boot.starter.rocketmq`
- 类名前缀：`MQ*` → `RocketMQ*`（如 `MQAutoConfiguration` → `RocketMQAutoConfiguration`）

**涉及文件**：
- `sloth-boot-starter-mq/` 整个目录重命名
- 所有 Java 文件的 package 声明和 import
- `sloth-boot-dependencies/pom.xml` 中的 artifactId
- `sloth-boot-starter/pom.xml` 中的 module 声明
- `sloth-boot-example-service/pom.xml` 中的依赖声明
- 各 README.md 中的引用

#### 6.2.3 MybatisPlusAutoConfiguration 重命名

**变更**：`MybatisPlusAutoConfiguration` → `MybatisAutoConfiguration`

**涉及文件**：`starter-mybatis/.../config/MybatisPlusAutoConfiguration.java`

#### 6.2.4 HealthIndicator 子包统一

**变更**：
- `starter-es/.../monitoring/` 中的 HealthIndicator → 移到 `health/`
- `starter-mq/.../monitor/` 中的 HealthIndicator → 移到 `health/`

#### 6.2.5 Metrics 子包统一

**变更**：
- `starter-thread-pool/.../monitor/` 中的 Metrics → 移到 `metrics/`

### 6.3 低优先级：依赖与清理

#### 6.3.1 parent POM 依赖下沉

**从 `sloth-boot-parent` 移除**：
- `hutool-core`、`guava`、`jackson-*`、`mapstruct*`、`transmittable-thread-local`、`jakarta.servlet-api`

**移入 `sloth-boot-common-core`**：上述所有依赖

**注意**：`common-core` 的 pom.xml 已有部分声明（hutool、guava），需确认无重复。

#### 6.3.2 内部模块版本声明清理

**变更**：所有 pom.xml 中引用内部模块的 `<version>${revision}</version>` 删除，由 BOM 统一管理。

#### 6.3.3 统一 "sloth:" 缓存前缀来源

**决策**：`RedisProperties.keyPrefix` 独立管理（因为它是配置化的，支持用户自定义），`CacheConstant.DEFAULT_CACHE_PREFIX` 删除。避免同一个值在两个地方定义。

#### 6.3.4 spring.factories 保留

**决策**：保留 `common-test` 的 `spring.factories`（`TestExecutionListener` 注册确实需要它），添加注释说明原因。

## 7. 执行顺序

整改按以下顺序执行，每步可独立验证：

1. **规范文档定稿**（本文档）
2. **依赖管理清理**（parent POM 依赖下沉、版本声明清理）
3. **高优先级边界修复**（移动 Servlet 代码、DesensitizeUtil、HealthIndicator 归属、CacheConstant 清理）
4. **命名一致性修正**（thread-pool 包名、mq→rocketmq、MybatisPlus→Mybatis、子包名统一）
5. **编译验证**：`mvn clean compile` 确保无编译错误
6. **测试验证**：`mvn test` 确保无测试失败

## 8. 风险评估

| 风险 | 影响 | 缓解措施 |
|------|------|---------|
| 移动类导致外部引用断裂 | 中 | 这是脚手架项目，外部用户少，直接改 |
| mq→rocketmq 重命名范围大 | 中 | 全局搜索替换 + 编译验证 |
| 依赖下沉后某些模块缺少依赖 | 低 | 编译验证即可发现 |
| thread-pool 包名变更影响使用者 | 低 | 脚手架项目，直接改 |
