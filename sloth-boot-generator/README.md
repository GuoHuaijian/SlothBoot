# sloth-boot-generator

基于 MyBatis-Plus Generator 封装的代码生成器，支持一键生成 Entity / Mapper / Service / Controller 全套 CRUD 代码。

## 快速使用

### API 方式

```java
GeneratorConfig config = new GeneratorConfig();
config.setUrl("jdbc:mysql://localhost:3306/mydb");
config.setUsername("root");
config.setPassword("root");
config.setTableNames(new String[]{"sys_user", "sys_role"});
config.setOutputDir("/path/to/project");
config.setParentPackage("com.example");
config.setModuleName("system");
config.setAuthor("your-name");

new CodeGenerator(config).generate();
```

### 命令行方式

```bash
java -jar sloth-boot-generator.jar \
  jdbc:mysql://localhost:3306/mydb \
  root \
  root \
  ./output \
  sys_user sys_role
```

### 快速生成

```java
CodeGenerator.quickGenerate(
    "jdbc:mysql://localhost:3306/mydb",
    "root", "root",
    new String[]{"sys_user"},
    "./output"
);
```

## 配置说明

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `parentPackage` | `com.sloth.boot` | 父包名 |
| `moduleName` | `system` | 模块名 |
| `tablePrefixes` | `["sys_", "biz_"]` | 表名前缀（生成时移除） |
| `author` | `sloth-boot` | 作者名 |
| `lombok` | `true` | 是否使用 Lombok |
| `swagger` | `true` | 是否生成 Swagger 注解 |
| `baseEntity` | `true` | Entity 是否继承 BaseEntity |

## 生成产物

以 `sys_user` 表为例：

```
com.example.system
├── domain.entity
│   └── User.java              # 实体类
├── mapper
│   └── UserMapper.java        # Mapper 接口
├── service
│   ├── UserService.java       # Service 接口
│   └── impl
│       └── UserServiceImpl.java  # Service 实现
└── controller
    └── UserController.java    # REST 控制器

resources/mapper
└── UserMapper.xml             # Mapper XML
```
