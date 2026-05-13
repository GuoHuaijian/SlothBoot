# sloth-boot-common-log

> SlothBoot 日志增强模块，提供 TraceId 过滤器、HTTP 请求日志记录、操作日志 AOP 切面。

## Maven 依赖

```xml
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-common-log</artifactId>
</dependency>
```

## 核心组件

| 组件 | 说明 |
|------|------|
| `TraceIdFilter` | Servlet 过滤器，自动生成/传递 TraceId 并写入 MDC |
| `HttpRequestLogFilter` | HTTP 请求日志过滤器，记录请求方法、URI、状态码、耗时 |
| `OperationLogAspect` | 操作日志 AOP 切面，基于 `@OperationLog` 注解记录业务操作 |

## 使用示例

### TraceId

TraceId 会自动在每个请求中生成并注入 MDC，配合 Logback 的 `%X{traceId}` 即可在日志中输出：

```xml
<pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] traceId=%X{traceId} %-5level %logger{36} - %msg%n</pattern>
```

### 操作日志

```java
@OperationLog(module = "用户管理", type = "新增", description = "新增用户")
public void createUser(UserDTO dto) {
    // 业务逻辑
}
```

## 配置说明

```yaml
sloth:
  log:
    enabled: true                    # 是否启用日志增强
    request-log-enabled: true        # 是否启用 HTTP 请求日志
    operation-log-enabled: true      # 是否启用操作日志
    exclude-urls:                    # 请求日志排除路径
      - /actuator/**
      - /health
```
