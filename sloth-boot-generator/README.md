# sloth-boot-generator

Sloth Boot 代码生成器：从数据库表结构一键生成与项目 COLA 分层约定完全一致的全套业务代码。

> 生成物风格与 `sloth-boot-example-service` 对齐：Controller + Command/Query 分离、
> PO 继承 starter-mybatis 的 `BaseEntity`、Mapper 继承 `BaseMapperX`、
> springdoc 注解、jakarta 校验注解、MapStruct 转换器、统一错误码枚举。

## 快速开始

### API 方式

```java
GeneratorConfig config = new GeneratorConfig();
config.setUrl("jdbc:mysql://localhost:3306/sloth_boot?useInformationSchema=true");
config.setUsername("root");
config.setPassword("root");
config.setRootPackage("com.example.app");
config.setModuleName("user");
config.setTableNames(List.of("sys_user", "sys_role"));

GenerationResult result = new CodeGenerator(config).generate();
System.out.println(result.summarize());
```

> MySQL 建议在 URL 追加 `useInformationSchema=true`，否则读不到列注释。

### 配置文件方式（命令行）

```properties
# sloth-generator.properties
url=jdbc:mysql://localhost:3306/sloth_boot?useInformationSchema=true
username=root
password=root
rootPackage=com.example.app
moduleName=user
tableNames=sys_user,sys_role
author=your-name
generateErrorCode=false
```

```bash
java -cp sloth-boot-generator.jar com.sloth.boot.generator.core.CodeGenerator \
  --config sloth-generator.properties
```

### 位置参数方式（其余配置全部默认）

```bash
java -cp sloth-boot-generator.jar com.sloth.boot.generator.core.CodeGenerator \
  jdbc:mysql://localhost:3306/sloth_boot root root sys_user sys_role
```

## 生成产物

以 `sys_user` 表（注释"系统用户表"，模块名 `user`）为例：

```
com.example.app
├── adapter/controller/user/
│   └── UserController.java              # REST 接口（新增/更新/删除/详情/分页）
├── application/
│   ├── command/user/
│   │   ├── UserSaveCommand.java         # 新增命令（@Transactional）
│   │   ├── UserUpdateCommand.java       # 更新命令（含存在性校验）
│   │   └── UserDeleteCommand.java       # 删除命令
│   ├── query/user/
│   │   ├── UserGetQuery.java            # 详情查询
│   │   └── UserPageQuery.java           # 分页查询（LambdaQueryWrapperX 条件构造）
│   └── model/
│       ├── form/user/UserForm.java      # 表单对象（@NotBlank/@NotNull/@Size 校验）
│       ├── query/user/UserQry.java      # 分页查询参数（继承 BaseQuery）
│       ├── vo/user/UserVO.java          # 视图对象（Long 序列化为 String 防精度丢失）
│       ├── convert/user/UserConvert.java # MapStruct 转换器
│       └── enums/user/UserErrorCode.java # 错误码枚举（NOT_FOUND / ALREADY_EXISTS）
└── infrastructure/
    ├── model/po/user/User.java          # 实体（继承 BaseEntity，仅含业务列）
    └── repository/mapper/user/
        ├── UserMapper.java              # 继承 BaseMapperX
        └── UserMapper.xml               # resultMap + Base_Column_List
```

REST 路径自动推导：`sys_user` → 类名 `User` → `/api/users`（内置常见英文复数规则）。

错误码编号规则：`errorCodePrefix(1001)` × 100 + 起始序号 → 100101、100102…，
每张表占用两个号（NOT_FOUND + ALREADY_EXISTS）。

## 架构

```
GeneratorConfig
      │
      ▼
DatabaseMetadataReader ──► TableDefinition（JDBC 元数据，不依赖具体数据库）
      │
      ▼
ModelFactory ──► TableModel（命名推导 / 字段过滤 / import 收集，模板零逻辑）
      │
      ▼
Artifact 注册表 × VelocityTemplateEngine（严格引用模式）
      │
      ▼
FileSystemOutputWriter ──► GenerationResult（写入/跳过状态报告）
```

- **metadata**：基于 `DatabaseMetaData` 读取表/列/主键/唯一索引，MySQL/H2 等通用。
- **naming**：表名→类名、列名→字段名、复数化资源路径、JDBC 类型→Java 类型映射。
- **model**：所有判断在工厂完成，模板只做渲染；import 按产物分别收集并排序去重。
- **artifact**：新增产物只需在枚举追加一项并配套 `templates/*.vm`，编排器零改动。

## 常用配置项

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `rootPackage` | `com.example.app` | 业务模块根包 |
| `moduleName` | `system` | 模块名（各分层包最后一级） |
| `tablePrefixes` | `[sys_, biz_]` | 移除的表名前缀 |
| `mapperXmlSamePackage` | `true` | XML 与接口同包（Sloth Boot 约定） |
| `extendsBaseEntity` | `true` | PO 继承 BaseEntity 并排除基类列 |
| `generateCommand` / `generateQuery` | `true` | Command / Query 生成开关 |
| `generateForm` / `generateQry` / `generateVo` / `generateConvert` | `true` | 各模型对象开关 |
| `generateErrorCode` | `true` | 错误码枚举开关 |
| `swaggerAnnotations` | `true` | @Tag/@Operation/@Schema 开关 |
| `validationAnnotations` | `true` | jakarta 校验注解开关 |
| `operateLogFqcn` | common-log 注解 | 置空可关闭 @OperateLog 生成 |
| `baseEntityFqcn` 等 | starter-mybatis 实现 | 可替换为任意基类 FQCN |
| `fileOverride` | `false` | 已存在文件是否覆盖 |
| `errorCodePrefix` / `errorCodeStart` | `1001` / `1` | 错误码分段与起始序号 |

降级逻辑：关闭 Form 或 Convert 时 Command 直接操作 PO；关闭 VO 或 Convert 时查询直接返回实体。

## 测试

模块内置三类测试，不依赖外部 MySQL：

- `NamingRulesTest` / `JdbcTypeMapperTest`：命名与类型映射规则。
- `ModelFactoryTest`：字段过滤、错误码编号、import 收集等纯逻辑。
- `CodeGeneratorEndToEndTest`：H2 内存库全链路（元数据读取 → 14 类产物落盘 → 内容断言 → 覆盖语义）。
