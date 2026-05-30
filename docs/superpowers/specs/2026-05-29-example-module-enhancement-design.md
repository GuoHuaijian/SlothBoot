# 示例模块增强设计

**日期:** 2026-05-29
**范围:** sloth-boot-example-service

## 目标

1. **规范化包结构** — 按领域分包，请求/响应/VO 分离
2. **新增 MyBatis ORM 全链路演示** — SysDept + SysUser，H2 内存数据库，覆盖 starter-mybatis 全部核心能力
3. **保持轻量** — 只依赖 Redis + H2，clone 即可运行

---

## 一、包结构重组

### 当前问题

- 所有 DTO/VO/Request/Event 混放在 `dto/` 包
- `UserVO` 在 `dto/` 而非 `vo/`
- `OrderStatusEvent` 是事件对象，不是 DTO
- 没有按领域分组，全部扁平

### 目标结构

```
com.sloth.boot.example/
├── Application.java
├── controller/                          # 按领域分子包
│   ├── ai/
│   │   └── AiController.java
│   ├── dept/                            # NEW
│   │   └── DeptController.java
│   ├── monitor/
│   │   └── MonitorController.java
│   ├── order/
│   │   └── OrderController.java
│   ├── product/
│   │   └── ProductController.java
│   ├── security/
│   │   └── SecurityController.java
│   ├── system/
│   │   └── SystemController.java
│   └── user/                            # NEW
│       └── UserController.java
├── domain/                              # NEW: MyBatis 实体 + Mapper
│   ├── entity/
│   │   ├── SysDept.java
│   │   └── SysUser.java
│   └── mapper/
│       ├── SysDeptMapper.java
│       └── SysUserMapper.java
├── service/                             # 按领域分子包
│   ├── ai/
│   │   └── AiDemoService.java
│   ├── dept/                            # NEW
│   │   └── DeptService.java
│   ├── monitor/
│   │   └── MonitorDemoService.java
│   ├── order/
│   │   └── OrderDemoService.java
│   ├── product/
│   │   └── ProductDemoService.java
│   ├── security/
│   │   └── SecurityDemoService.java
│   ├── system/
│   │   └── SystemDemoService.java
│   └── user/                            # NEW
│       └── UserService.java
└── model/                               # 替代原 dto/ 包
    ├── ai/                              # (原 AiController 无 DTO)
    ├── monitor/
    │   ├── request/                     # (MonitorController 无请求体)
    │   └── vo/
    │       ├── JvmInfo.java
    │       └── MetricSummary.java
    ├── order/
    │   ├── request/
    │   │   └── OrderCreateRequest.java
    │   ├── dto/
    │   │   └── OrderDTO.java
    │   └── event/
    │       └── OrderStatusEvent.java
    ├── product/
    │   ├── request/
    │   │   └── ProductCreateRequest.java
    │   └── dto/
    │       └── ProductDTO.java
    ├── security/
    │   ├── request/
    │   │   └── CryptoRequest.java
    │   └── vo/
    │       └── CryptoResponse.java
    ├── system/
    │   ├── request/
    │   │   └── LoginRequest.java
    │   └── vo/
    │       ├── LoginResponse.java
    │       └── UserVO.java
    ├── dept/                            # NEW
    │   ├── request/
    │   │   └── DeptCreateRequest.java
    │   └── vo/
    │       └── DeptVO.java
    └── user/                            # NEW
        ├── request/
        │   ├── UserCreateRequest.java
        │   └── UserQuery.java
        └── vo/
            └── UserVO.java
```

### 迁移映射

| 原文件 | 新位置 |
|--------|--------|
| `dto/CryptoRequest.java` | `model/security/request/CryptoRequest.java` |
| `dto/CryptoResponse.java` | `model/security/vo/CryptoResponse.java` |
| `dto/JvmInfo.java` | `model/monitor/vo/JvmInfo.java` |
| `dto/LoginRequest.java` | `model/system/request/LoginRequest.java` |
| `dto/LoginResponse.java` | `model/system/vo/LoginResponse.java` |
| `dto/MetricSummary.java` | `model/monitor/vo/MetricSummary.java` |
| `dto/OrderCreateRequest.java` | `model/order/request/OrderCreateRequest.java` |
| `dto/OrderDTO.java` | `model/order/dto/OrderDTO.java` |
| `dto/OrderStatusEvent.java` | `model/order/event/OrderStatusEvent.java` |
| `dto/ProductCreateRequest.java` | `model/product/request/ProductCreateRequest.java` |
| `dto/ProductDTO.java` | `model/product/dto/ProductDTO.java` |
| `dto/UserVO.java` | `model/system/vo/UserVO.java` |
| `controller/AiController.java` | `controller/ai/AiController.java` |
| `controller/MonitorController.java` | `controller/monitor/MonitorController.java` |
| `controller/OrderController.java` | `controller/order/OrderController.java` |
| `controller/ProductController.java` | `controller/product/ProductController.java` |
| `controller/SecurityController.java` | `controller/security/SecurityController.java` |
| `controller/SystemController.java` | `controller/system/SystemController.java` |
| `service/AiDemoService.java` | `service/ai/AiDemoService.java` |
| `service/MonitorDemoService.java` | `service/monitor/MonitorDemoService.java` |
| `service/OrderDemoService.java` | `service/order/OrderDemoService.java` |
| `service/ProductDemoService.java` | `service/product/ProductDemoService.java` |
| `service/SecurityDemoService.java` | `service/security/SecurityDemoService.java` |
| `service/SystemDemoService.java` | `service/system/SystemDemoService.java` |

---

## 二、MyBatis ORM 全链路演示

### 领域模型

**SysDept（部门）** + **SysUser（用户）**

#### SysDept 实体

```java
// 继承 MyBatis BaseEntity（自动填充、逻辑删除、乐观锁、雪花ID）
// 继承 TreeNode（id/parentId/children/sort）
@Table("sys_dept")
public class SysDept extends BaseEntity implements TreeNode {
    private String name;
    private Long parentId;
    private String leader;
    @EnumValue(intValues = {0, 1})
    private Integer status;
    private String ancestors;
}
```

**演示能力:**
- `BaseEntity` — 自动填充 createTime/updateTime/createBy/updateBy、@TableLogic 逻辑删除、@Version 乐观锁、雪花ID
- `TreeNode` — 继承 id/parentId/children/sort，配合 `TreeUtil.buildTree()` 构建部门树
- `@EnumValue` — status 字段校验

#### SysUser 实体

```java
@Table("sys_user")
public class SysUser extends BaseEntity {
    private Long deptId;
    private String username;
    @TableField(typeHandler = EncryptTypeHandler.class)
    private String phone;              // AES 加密存储
    @TableField(typeHandler = EncryptTypeHandler.class)
    private String idCard;             // AES 加密存储
    private String email;
    @EnumValue(intValues = {0, 1, 2})
    private Integer gender;
    @EnumValue(intValues = {0, 1})
    private Integer status;
    @TableField(typeHandler = JsonTypeHandler.class)
    private Map<String, Object> extraInfo;  // JSON 列
}
```

**演示能力:**
- `EncryptTypeHandler` — phone、idCard 字段 AES 加密存入 DB，读取时自动解密
- `JsonTypeHandler` — extraInfo 以 JSON 字符串存储在 TEXT 列，读取时自动反序列化为 Map
- `@EnumValue` — gender、status 字段校验

### Mapper 层

```java
@Mapper
public interface SysDeptMapper extends BaseMapperX<SysDept> {
    int insertBatch(@Param("list") List<SysDept> list);

    @DataScope(deptAlias = "d")
    List<SysDept> selectListWithScope(@Param("ew") Wrapper<SysDept> wrapper);
}

@Mapper
public interface SysUserMapper extends BaseMapperX<SysUser> {
    int insertBatch(@Param("list") List<SysUser> list);

    @DataPermission(deptAlias = "u", userAlias = "u")
    Page<SysUser> selectPageWithPermission(Page<SysUser> page, @Param("ew") Wrapper<SysUser> wrapper);
}
```

**演示能力:**
- `BaseMapperX` — selectPage(BaseQuery)、selectOne(field, value)
- `insertBatch` — 由 InsertBatchSqlInjector 自动注入的单语句批量插入
- `@DataScope` — 传统数据权限，根据 UserContext.getDataScope() 自动追加 WHERE
- `@DataPermission` — 增强型数据权限，支持 SpEL 表达式

### 请求/响应对象

#### DeptCreateRequest
```java
@Data
public class DeptCreateRequest {
    @NotBlank
    private String name;
    private Long parentId;
    private String leader;
    @EnumValue(intValues = {0, 1})
    private Integer status;
}
```

#### DeptVO
```java
@Data
public class DeptVO extends BaseVO {
    private Long id;
    private String name;
    private Long parentId;
    private String leader;
    private Integer status;
    private String ancestors;
    private List<DeptVO> children;
}
```

#### UserCreateRequest
```java
@Data
public class UserCreateRequest {
    @NotBlank
    private String username;
    @Phone
    private String phone;
    @IdCard
    private String idCard;
    private String email;
    @EnumValue(intValues = {0, 1, 2})
    private Integer gender;
    @EnumValue(intValues = {0, 1})
    private Integer status;
    private Long deptId;
    private Map<String, Object> extraInfo;
}
```

#### UserQuery
```java
@Data
public class UserQuery extends BaseQuery {
    private String username;
    private String phone;
    private Long deptId;
    private Integer status;
}
```

#### SysUserVO
```java
@Data
public class SysUserVO extends BaseVO {
    private Long id;
    private Long deptId;
    private String username;
    @Desensitize(type = DesensitizeType.MOBILE)
    private String phone;
    @Desensitize(type = DesensitizeType.ID_CARD)
    private String idCard;
    private String email;
    private Integer gender;
    private Integer status;
    private Map<String, Object> extraInfo;
}
```

### Service 层

#### DeptService
- `create(DeptCreateRequest)` — 创建部门，@OperateLog 审计
- `getTree()` — 查询全部部门，TreeUtil.buildTree 构建树
- `getById(id)` — BaseMapperX.selectById
- `update(dept)` — 乐观锁更新（@Version）
- `deleteById(id)` — @TableLogic 逻辑删除
- `batchImport(list)` — insertBatch 批量导入

#### UserService
- `create(UserCreateRequest)` — 加密存储 phone/idCard
- `page(UserQuery)` — BaseMapperX.selectPage + LambdaQueryWrapperX 动态过滤（likeIfPresent/eqIfPresent/inIfPresent）
- `getById(id)` — EncryptTypeHandler 自动解密返回
- `getVO(id)` — 返回 SysUserVO，@Desensitize 脱敏
- `update(user)` — 乐观锁更新
- `deleteById(id)` — 逻辑删除
- `batchImport(list)` — insertBatch 批量导入
- `pageWithScope(UserQuery)` — @DataPermission 数据权限演示

### Controller 层

#### DeptController `/api/dept`
| 方法 | 路径 | 演示能力 |
|------|------|---------|
| POST | `/` | 创建部门，@OperateLog |
| GET | `/tree` | TreeUtil.buildTree |
| GET | `/{id}` | BaseMapperX.selectById |
| PUT | `/` | @Version 乐观锁 |
| DELETE | `/{id}` | @TableLogic 逻辑删除 |
| POST | `/import` | insertBatch |

#### UserController `/api/user`
| 方法 | 路径 | 演示能力 |
|------|------|---------|
| POST | `/` | @Phone/@IdCard/@EnumValue 校验 + EncryptTypeHandler 加密存储 |
| GET | `/page` | selectPage + LambdaQueryWrapperX 动态过滤 |
| GET | `/{id}` | EncryptTypeHandler 自动解密 |
| GET | `/{id}/desensitize` | @Desensitize 脱敏 VO |
| PUT | `/` | @Version 乐观锁 |
| DELETE | `/{id}` | @TableLogic 逻辑删除 |
| POST | `/import` | insertBatch 批量导入 |
| GET | `/scope` | @DataPermission 数据权限 |

### 数据库初始化

#### schema.sql (H2 DDL)
```sql
CREATE TABLE IF NOT EXISTS sys_dept (
    id          BIGINT PRIMARY KEY,
    name        VARCHAR(64) NOT NULL,
    parent_id   BIGINT      DEFAULT 0,
    sort        INT         DEFAULT 0,
    leader      VARCHAR(64),
    status      TINYINT     DEFAULT 0,
    ancestors   VARCHAR(512) DEFAULT '',
    create_by   VARCHAR(64),
    create_time TIMESTAMP,
    update_by   VARCHAR(64),
    update_time TIMESTAMP,
    deleted     TINYINT     DEFAULT 0,
    version     INT         DEFAULT 1
);

CREATE TABLE IF NOT EXISTS sys_user (
    id          BIGINT PRIMARY KEY,
    dept_id     BIGINT,
    username    VARCHAR(64)  NOT NULL,
    phone       VARCHAR(128),
    id_card     VARCHAR(128),
    email       VARCHAR(128),
    gender      TINYINT      DEFAULT 0,
    status      TINYINT      DEFAULT 0,
    extra_info  TEXT,
    create_by   VARCHAR(64),
    create_time TIMESTAMP,
    update_by   VARCHAR(64),
    update_time TIMESTAMP,
    deleted     TINYINT      DEFAULT 0,
    version     INT          DEFAULT 1
);
```

#### data.sql (初始数据)
- 3 个部门：总公司(1) / 技术部(2, parent=1) / 产品部(3, parent=1)
- 3 个用户：admin(dept=1)、dev_user(dept=2)、product_user(dept=3)

---

## 三、配置变更

### pom.xml
新增 H2 依赖：
```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```
移除 MySQL 依赖（或保留，scope 改为 optional，切换时不需改 pom）。

### application.yml 变更
1. **移除** MyBatis 和 DataSource 的 autoconfigure exclude
2. **替换** MySQL 数据源为 H2 内存模式：
```yaml
spring:
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:sloth_boot;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=FALSE
    username: sa
    password:
  sql:
    init:
      mode: always
      schema-locations: classpath:schema.sql
      data-locations: classpath:data.sql
  h2:
    console:
      enabled: true
      path: /h2-console
```
3. **新增** encrypt-key 配置：
```yaml
sloth:
  mybatis:
    encrypt-key: sloth-boot-example-key-2024
```
4. **更新** doc.base-packages 扫描路径（新增 controller 子包）

---

## 四、框架能力覆盖清单

| # | 框架能力 | 对应文件/端点 |
|---|----------|-------------|
| 1 | MyBatis BaseEntity（雪花ID + 自动填充 + 逻辑删除 + 乐观锁） | SysDept / SysUser 实体 |
| 2 | BaseMapperX.selectPage(BaseQuery) | GET /api/user/page |
| 3 | LambdaQueryWrapperX（likeIfPresent/eqIfPresent/betweenIfPresent） | UserService.page() |
| 4 | EncryptTypeHandler（AES 加密字段） | SysUser.phone / idCard |
| 5 | JsonTypeHandler（JSON 列） | SysUser.extraInfo |
| 6 | insertBatch（单语句批量插入） | POST /api/dept/import, /api/user/import |
| 7 | @DataScope（传统数据权限） | SysDeptMapper.selectListWithScope |
| 8 | @DataPermission（增强数据权限） | SysUserMapper.selectPageWithPermission |
| 9 | SlowSqlInterceptor | 复杂查询自然触发 |
| 10 | PaginationInnerInterceptor | 分页查询自动生效 |
| 11 | OptimisticLockerInnerInterceptor | PUT 更新时 version 条件 |
| 12 | BlockAttackInnerInterceptor | 阻止无条件全表更新 |
| 13 | @Phone 校验 | UserCreateRequest.phone |
| 14 | @IdCard 校验 | UserCreateRequest.idCard |
| 15 | @EnumValue 校验 | UserCreateRequest.gender/status, DeptCreateRequest.status |
| 16 | @Desensitize 脱敏 | SysUserVO.phone/idCard，GET /api/user/{id}/desensitize |
| 17 | TreeNode + TreeUtil | SysDept + GET /api/dept/tree |
| 18 | BaseVO | DeptVO / SysUserVO |
| 19 | BaseQuery | UserQuery extends BaseQuery |
| 20 | PageResult | 分页返回 |
| 21 | @OperateLog | POST/PUT/DELETE 操作审计 |
| 22 | UserContext | 自动填充 createBy/updateBy |
