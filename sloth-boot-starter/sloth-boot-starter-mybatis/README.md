# Sloth Boot Starter MyBatis

## 简介

`sloth-boot-starter-mybatis` 基于 MyBatis-Plus 进行增强，提供扩展 Mapper 基类（`BaseMapperX`）、自动填充、数据权限（`@DataScope`）、慢 SQL 拦截器、JSON/加密类型处理器和批量插入注入器等能力。

## Maven 依赖

```xml
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-starter-mybatis</artifactId>
</dependency>
```

## 配置项

| 配置键 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `sloth.mybatis.tenant-enabled` | boolean | `false` | 是否启用多租户插件 |
| `sloth.mybatis.tenant-column` | String | `tenant_id` | 租户字段列名 |
| `sloth.mybatis.tenant-ignore-tables` | Set | `[]` | 忽略租户过滤的表名 |
| `sloth.mybatis.slow-sql-threshold` | long | `1000` | 慢 SQL 告警阈值（毫秒） |

## 核心组件

| 组件 | 说明 |
|------|------|
| `BaseMapperX<T>` | 扩展 Mapper 基类，内置分页查询、按字段查询、批量插入 |
| `LambdaQueryWrapperX<T>` | Lambda 条件构造器增强 |
| `BaseEntity` | 实体基类，包含 id、createBy、createTime、updateBy、updateTime 等字段 |
| `AutoFillMetaObjectHandler` | 自动填充处理器，自动填充 createTime、updateTime 等字段 |
| `@DataScope` | 数据权限注解，自动追加部门/用户维度的 WHERE 条件 |
| `DataScopeInterceptor` | 数据权限拦截器，支持 all/dept/dept_and_below/self 四种范围 |
| `SlowSqlInterceptor` | 慢 SQL 拦截器，超过阈值打印 WARN 日志 |
| `JsonTypeHandler` | JSON 类型处理器，数据库 JSON 字段与 Java 对象自动互转 |
| `EncryptTypeHandler` | 加密类型处理器 |
| `InsertBatchSqlInjector` | 批量插入 SQL 注入器，支持真正的批量 INSERT |
| 分页/乐观锁/防全表更新/租户 插件 | MybatisPlusInterceptor 自动注册 |

## 使用示例

### BaseMapperX 使用

```java
// Mapper 继承 BaseMapperX
@Mapper
public interface UserMapper extends BaseMapperX<UserDO> {
}

@Service
public class UserService {

    private final UserMapper userMapper;

    // 分页查询
    public PageResult<UserDO> listUsers(UserQuery query) {
        LambdaQueryWrapperX<UserDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.likeIfPresent(UserDO::getName, query.getName())
               .eqIfPresent(UserDO::getStatus, query.getStatus());
        return userMapper.selectPage(query, wrapper);
    }

    // 按字段查询单条
    public UserDO getByPhone(String phone) {
        return userMapper.selectOne("phone", phone);
    }

    // 批量插入
    public int batchInsert(List<UserDO> users) {
        return userMapper.insertBatch(users);
    }
}
```

### @DataScope 数据权限

```java
@Mapper
public interface OrderMapper extends BaseMapperX<OrderDO> {

    // 查询订单时自动追加数据权限条件
    @DataScope(deptAlias = "o", userAlias = "u")
    @Select("SELECT o.* FROM sys_order o LEFT JOIN sys_user u ON o.create_by = u.user_id")
    List<OrderDO> selectOrderList();
}
```

数据范围类型（由 `UserContext.getDataScope()` 决定）：
- `all` - 全部数据（不追加条件）
- `dept` - 本部门数据
- `dept_and_below` - 本部门及以下数据
- `self` - 仅本人数据

### 多租户配置

```yaml
sloth:
  mybatis:
    tenant-enabled: true
    tenant-column: tenant_id
    tenant-ignore-tables:
      - sys_config
      - sys_dict
```

## FAQ

**Q: `BaseMapperX.selectPage` 和 MyBatis-Plus 原生分页有何区别？**
A: `BaseMapperX.selectPage` 接收 `BaseQuery` 参数，返回框架统一的 `PageResult<T>` 对象，无需手动构建 `Page` 对象。

**Q: 慢 SQL 日志在哪里查看？**
A: 慢 SQL 以 WARN 级别输出到应用日志，包含 SQL ID、耗时、SQL 语句和参数。默认阈值 1000ms，通过 `sloth.mybatis.slow-sql-threshold` 调整。

**Q: `IllegalSQLInnerInterceptor` 什么时候生效？**
A: 仅在 `dev` 环境 profile 下生效，用于开发阶段检测可能造成全表扫描的危险 SQL。

**Q: 多租户插件如何获取当前租户 ID？**
A: 从 `UserContext.getTenantId()` 获取，需确保拦截器或网关已将租户信息设置到 `UserContext` 中。

**Q: 批量插入使用的是真批量还是逐条插入？**
A: `BaseMapperX.insertBatch` 默认逐条插入。如需真批量（单条 INSERT 语句），使用 MyBatis-Plus 的 `insertBatchSomeColumn` 注入器，已由 `InsertBatchSqlInjector` 自动注册。
