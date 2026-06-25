# sloth-boot-example-service

`sloth-boot-example-service` 是 Sloth Boot 的单体示例服务，采用 **整洁架构**（Adapter / Application / Infrastructure 三层分离），演示各个基础 starter 的典型接入方式。

## 模块用途

这个示例工程主要用来说明以下几件事：

- 如何引入 `starter-web`
- 如何接入 `starter-ai`
- 如何接入 `starter-redis`
- 如何接入 `starter-mybatis`
- 如何启用线程池、监控和接口文档
- 如何组织 `application.yml`、`application-dev.yml` 和日志配置
- **如何在单体架构中实践整洁架构，实现高内聚、低耦合的代码组织**

## 整洁架构设计

本模块采用 **整洁架构**，在单体应用中实现了清晰的职责分离和关注点分离，为后续演进到微服务做好准备。

### 核心设计原则

1. **分层职责明确**：每一层都有清晰的边界和职责，避免职责混乱
2. **依赖方向单一**：Adapter → Application → Infrastructure，禁止反向依赖
3. **Command/Query 分离**：读写分离，业务逻辑更清晰
4. **按业务分包**：相同业务的代码放在一起，便于模块化和扩展

### 分层职责

```
┌─────────────────────────────────────────────────────────────┐
│  Adapter（入口适配层）                                         │
│  - Controller：处理 HTTP 请求，参数校验，调用 Application 层     │
│  - Consumer：处理 MQ 消息                                    │
│  - Scheduler：定时任务                                       │
│  职责：协议转换、参数校验、调用编排                               │
└─────────────────────────────────────────────────────────────┘
                              ↓ 依赖
┌─────────────────────────────────────────────────────────────┐
│  Application（业务逻辑层）                                     │
│  - Command：写操作（创建、更新、删除）                           │
│  - Query：读操作（查询、分页）                                  │
│  - Helper：复杂业务逻辑、对象组装器、工具类                          │
│  - Model：业务数据模型（Form/VO/DTO/Convert/Event/Enum）        │
│  职责：业务用例实现、领域规则编排                                 │
└─────────────────────────────────────────────────────────────┘
                              ↓ 依赖
┌─────────────────────────────────────────────────────────────┐
│  Infrastructure（基础设施层）                                   │
│  - Repository：存储访问（Mapper/Cache/ES）                     │
│  - Model：持久化模型（PO）                                      │
│  - Config：技术配置                                            │
│  职责：技术实现细节、外部系统集成                                 │
└─────────────────────────────────────────────────────────────┘
```

## 目录结构

```
com.sloth.boot.example
├── adapter/                              # 入口适配层
│   ├── controller/                       # HTTP 入口（每个端点一个 Controller）
│   │   ├── user/                         # 用户相关接口
│   │   ├── dept/                         # 部门相关接口
│   │   ├── order/                        # 订单相关接口
│   │   ├── product/                      # 商品相关接口
│   │   ├── auth/                         # 认证相关接口
│   │   ├── security/                     # 安全相关接口
│   │   ├── ai/                           # AI 相关接口
│   │   ├── monitor/                      # 监控相关接口
│   │   └── redis/                        # Redis 演示接口
│   ├── consumer/                         # MQ 消费者
│   │   └── order/                        # 订单状态事件消费者
│   └── scheduler/                        # 定时任务
│       └── user/                         # 用户统计作业
│
├── application/                          # 业务逻辑层
│   ├── command/                          # 写操作（按业务分包）
│   │   ├── user/                         # RegisterUserCommand, ModifyUserCommand, ...
│   │   ├── dept/                         # CreateDeptCommand, UpdateDeptCommand, ...
│   │   ├── order/                        # PlaceOrderCommand, PayOrderCommand
│   │   ├── product/                      # AddProductCommand, DeleteProductCommand
│   │   ├── auth/                         # AuthCommand（登录/登出/权限）
│   │   ├── security/                     # SecurityCommand（加解密演示）
│   │   ├── ai/                           # AiCommand（AI 对话）
│   │   ├── monitor/                      # MonitorCommand（监控查询）
│   │   └── redis/                        # RedisDemoCommand（Redis 能力演示）
│   ├── query/                            # 读操作（按业务分包）
│   │   ├── user/                         # UserQuery, PageUserQuery, PageUserPermissionQuery
│   │   ├── dept/                         # DeptQuery, DeptTreeQuery, DeptScopeQuery
│   │   ├── order/                        # ListOrdersQuery
│   │   └── product/                      # ListProductsQuery
│   ├── helper/                           # 复杂业务逻辑、对象组装器、工具类
│   │   ├── order/                        # OrderAssembler（订单组装）
│   │   └── dept/                         # DeptAssembler（部门组装 + 祖级路径计算）
│   └── model/                            # 业务数据模型
│       ├── form/                         # 表单对象（HTTP 入参，带校验注解）
│       │   ├── user/                     # UserCreateForm, UserUpdateForm
│       │   ├── dept/                     # DeptCreateForm, DeptUpdateForm
│       │   ├── order/                    # OrderCreateForm
│       │   ├── product/                  # ProductCreateForm
│       │   ├── auth/                     # LoginForm
│       │   └── security/                 # CryptoRequest
│       ├── vo/                           # 视图对象（接口响应，带脱敏注解）
│       │   ├── user/                     # SysUserVO
│       │   ├── dept/                     # DeptVO（继承 TreeNode）
│       │   ├── order/                    # OrderVO
│       │   ├── product/                  # ProductVO
│       │   ├── auth/                     # LoginVO, SystemUserVO
│       │   └── security/                 # CryptoResponse
│       ├── dto/                          # 数据传输对象
│       ├── convert/                      # 对象转换器（MapStruct）
│       │   ├── user/                     # UserConvert
│       │   ├── dept/                     # DeptConvert
│       │   ├── order/                    # OrderConvert
│       │   └── product/                  # ProductConvert
│       ├── event/                        # 领域事件
│       │   └── order/                    # OrderStatusEvent
│       ├── query/                        # 查询条件
│       │   └── user/                     # UserPageQry
│       └── enums/                        # 业务枚举（ErrorCode）
│           ├── user/                     # UserErrorCode
│           ├── dept/                     # DeptErrorCode
│           ├── order/                    # OrderErrorCode, OrderStatus
│           ├── product/                  # ProductErrorCode
│           └── auth/                     # AuthErrorCode
│
└── infrastructure/                       # 基础设施层
    ├── model/po/                         # 数据库实体
    │   ├── user/                         # SysUser（AES 加密字段）
    │   ├── dept/                         # SysDept
    │   ├── order/                        # DemoOrder
    │   └── product/                      # Product
    ├── repository/mapper/                # MyBatis Mapper
    │   ├── user/                         # SysUserMapper + XML
    │   ├── dept/                         # SysDeptMapper + XML
    │   ├── order/                        # OrderMapper + XML
    │   └── product/                      # ProductMapper + XML
    └── config/                           # 基础设施配置
        └── EmbeddedRedisConfig.java      # 内嵌 Redis 配置
```

## 代码组织示例

以 **用户模块** 为例，展示完整的分层：

```
user/
├── adapter/controller/user/
│   ├── RegisterUserController.java      # 注册用户（POST /api/users）
│   ├── QueryUserController.java         # 查询用户详情（GET /api/users/{id}）
│   ├── ModifyUserController.java        # 修改用户（PUT /api/users）
│   ├── RemoveUserController.java        # 删除用户（DELETE /api/users/{id}）
│   ├── ImportUsersController.java       # 批量导入（POST /api/users/import）
│   ├── PageUserQueryController.java     # 分页查询（GET /api/users/page）
│   └── PageUserPermissionController.java # 数据权限查询（GET /api/users/scope）
│
├── application/
│   ├── command/user/
│   │   ├── RegisterUserCommand.java     # 注册用户命令
│   │   ├── ModifyUserCommand.java       # 修改用户命令
│   │   ├── RemoveUserCommand.java       # 删除用户命令
│   │   └── ImportUsersCommand.java      # 批量导入命令
│   ├── query/user/
│   │   ├── UserQuery.java               # 查询单个用户
│   │   ├── PageUserQuery.java           # 分页查询用户
│   │   └── PageUserPermissionQuery.java # 数据权限分页查询
│   └── model/
│       ├── form/user/                   # UserCreateForm, UserUpdateForm
│       ├── vo/user/                     # SysUserVO（带脱敏注解）
│       ├── query/user/                  # UserPageQry（分页查询条件）
│       └── convert/user/                # UserConvert (MapStruct)
│
└── infrastructure/
    ├── model/po/user/
    │   └── SysUser.java                 # 用户实体（AES 加密字段）
    └── repository/mapper/user/
        ├── SysUserMapper.java           # Mapper 接口
        └── SysUserMapper.xml            # Mapper XML
```

## 依赖方向示例

```java
// ✅ 正确的依赖方向
@RestController
public class RegisterUserController {
    private final RegisterUserCommand command;  // Controller 依赖 Command
}

@Component
public class RegisterUserCommand {
    private final SysUserMapper userMapper;     // Command 依赖 Mapper
    private final UserConvert userConvert;       // Command 依赖 Convert
}

// ❌ 错误的依赖方向（禁止）
@Component
public class RegisterUserCommand {
    private final RegisterUserController controller;  // ❌ 反向依赖
}
```

## 运行前准备

1. 准备本地 `MySQL`（或使用默认 H2 内存数据库）
2. 准备本地 `Redis`（或使用内嵌 Redis，零配置启动）
3. 按需修改 `src/main/resources/application-dev.yml`

## 启动方式

在项目根目录执行：

```bash
mvn -pl sloth-boot-example/sloth-boot-example-service spring-boot:run
```

## 默认访问地址

- 应用健康检查：`GET /api/monitor/health`
- 接口文档入口：`GET /doc.html`
- Actuator 健康检查：`GET /actuator/health`

## 配置文件说明

- `application.yml`
  - 放通用配置示例
  - 展示常见 `sloth.*` 配置项
- `application-dev.yml`
  - 放开发环境数据库和 Redis 配置
- `logback-spring.xml`
  - 提供控制台、文件和生产环境 JSON 风格日志输出配置

## 特性演示

### 基础能力
- ✅ RESTful API 设计（一端口一 Controller）
- ✅ 参数校验（`@Valid` + `@NotBlank` + 自定义校验器）
- ✅ 统一响应格式（`R<T>`）
- ✅ 接口文档自动生成（Knife4j / SpringDoc）

### 数据访问
- ✅ MyBatis-Plus 增删改查
- ✅ 分页查询（`LambdaQueryWrapperX`）
- ✅ 批量操作（`insertBatch`）
- ✅ 字段加密（AES，`EncryptTypeHandler`）
- ✅ 数据权限（`@DataPermission` + `@DataScope`）
- ✅ 逻辑删除 + 乐观锁

### 对象转换
- ✅ MapStruct 集成（`@Mapper(componentModel = "spring")`）
- ✅ 列表转换（`toVOList` / `toEntityList`）

### 业务逻辑
- ✅ Command/Query 分离（读写分离）
- ✅ Helper 层（复杂业务逻辑、对象组装器）
- ✅ 领域事件（`OrderStatusEvent` + `@EventListener`）

### 中间件集成
- ✅ Redis 缓存（`RedisCacheUtil` + 多级缓存策略）
- ✅ Redis 分布式锁（`@DistributedLock`）
- ✅ Redis 布隆过滤器（防穿透）
- ✅ Redis Pub/Sub（事件驱动）
- ✅ 内嵌 Redis（零依赖启动）

### 安全能力
- ✅ Sa-Token 认证授权（登录/登出/权限校验）
- ✅ 数据脱敏（`@Desensitize`，手机号/身份证/邮箱）
- ✅ AES/RSA 加解密
- ✅ HMAC 签名验签
- ✅ XSS 防护（`XssCleaner`）
- ✅ 接口限流（`@RateLimit`）
- ✅ 接口幂等（`@Idempotent`）

### AI 能力
- ✅ Spring AI 集成
- ✅ OpenAI 对话
- ✅ 流式输出（SSE）
- ✅ 多轮记忆对话
- ✅ 结构化输出

### 运维监控
- ✅ Actuator 健康检查
- ✅ JVM 监控
- ✅ 线程池监控 + 动态调整
- ✅ Micrometer 指标
- ✅ 慢接口检测
- ✅ 操作日志（`@OperateLog`）
- ✅ XXL-Job 定时任务

### 事件驱动
- ✅ Spring Event（`@EventListener` + `@Async`）
- ✅ Redis Pub/Sub 事件

## 编码规范

本示例遵循以下规范：

- **类命名**：`UpperCamelCase`，如 `RegisterUserCommand`
- **方法命名**：`lowerCamelCase`，Command 统一使用 `execute()` 方法名
- **字段命名**：Controller 中字段名与类型名一致（如 `RegisterUserCommand createUserCommand`）
- **Javadoc**：所有公共类和方法必须有 `@author sloth-boot` `@since 1.0.0`
- **注解**：写操作 Controller 必须加 `@OperateLog`，`@RequestBody` 参数必须加 `@Valid`

## 适合怎么使用

- 如果你只想看 starter 的接入方式，直接从这个模块开始看
- 如果你要验证自己的改动有没有破坏主链路，可以优先用这个模块做冒烟测试
- 如果你准备在 GitHub 展示项目，这个模块也是最适合截图和写使用示例的地方
- **如果你想了解如何在单体应用中实践整洁架构，这个模块是最佳参考**

## 架构优势

1. **清晰的职责边界**：每一层都有明确的职责，便于理解和维护
2. **易于测试**：Command/Query 可以独立测试，依赖注入友好
3. **便于扩展**：新增业务模块时，按照现有结构创建目录即可
4. **团队协作友好**：不同开发者修改不同模块时，减少代码冲突
5. **微服务演进准备**：每个业务模块可以独立拆分为微服务
