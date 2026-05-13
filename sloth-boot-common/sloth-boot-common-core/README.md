# sloth-boot-common-core

> SlothBoot 核心基础模块，提供统一响应包装、异常体系、上下文传递、通用工具等基础设施。

## Maven 依赖

```xml
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-common-core</artifactId>
</dependency>
```

## 核心组件

| 组件 | 说明 |
|------|------|
| `R<T>` | 统一响应结果包装，自动携带 traceId |
| `PageResult<T>` | 分页响应结果 |
| `BaseEntity` | 通用实体基类（id, createTime, updateTime, createBy, updateBy, deleted, version） |
| `BaseQuery` | 通用分页查询基类 |
| `BizException` | 业务异常 |
| `SystemException` | 系统异常 |
| `GlobalErrorCode` | 全局错误码枚举 |
| `ErrorCode` | 错误码接口（业务方可自定义） |
| `TraceContext` | 追踪上下文（traceId, spanId），支持异步线程传递 |
| `UserContext` | 用户上下文（userId, username, tenantId, roles, dataScope, extra） |
| `I18nUtil` | 国际化工具类 |
| `SpelUtil` | SpEL 表达式解析工具 |
| `SpringContextUtil` | Spring 上下文工具 |

## 使用示例

### 统一响应

```java
// 成功
return R.ok(data);
return R.ok("自定义消息", data);

// 失败
return R.fail("错误消息");
return R.fail(400, "参数错误");
return R.fail(GlobalErrorCode.NOT_FOUND);
```

### 异常抛出

```java
// 业务异常（WARN 级别日志）
throw new BizException("用户不存在");
throw new BizException(40001, "余额不足");
throw new BizException(GlobalErrorCode.BAD_REQUEST);

// 系统异常（ERROR 级别日志）
throw new SystemException("数据库连接失败", cause);
```

### 用户上下文

```java
// 设置
UserContext.set(new UserContext.UserInfo()
    .setUserId(1L)
    .setUsername("admin")
    .setTenantId("tenant_001")
    .setRoles(Set.of("admin", "user")));

// 获取
Long userId = UserContext.getUserId();
String username = UserContext.getUsername();
Set<String> roles = UserContext.getRoles();
```

### 追踪上下文

```java
String traceId = TraceContext.generateTraceId();
TraceContext.set(new TraceContext.TraceInfo(traceId, null));

// 异步线程中也可获取（基于 TransmittableThreadLocal）
String currentTraceId = TraceContext.getTraceId();
```

### 国际化

```java
// 使用 i18n 消息
String msg = I18nUtil.getMessage("sloth.success");
String paramMsg = I18nUtil.getMessage("sloth.error.missing_param", "username");
```

## 自定义注解

| 注解 | 说明 |
|------|------|
| `@DataScope` | 数据权限范围标注 |
| `@EnumValue` | 枚举值校验 |
| `@Phone` | 手机号校验 |
| `@IdCard` | 身份证号校验 |
| `@SkipResponseWrapper` | 跳过统一响应包装 |
