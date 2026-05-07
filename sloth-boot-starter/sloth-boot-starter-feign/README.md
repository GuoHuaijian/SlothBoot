# Sloth Boot Starter Feign

Feign 远程调用增强组件，提供请求头自动透传、统一异常解码、响应包装及降级工厂模板。

## Maven 依赖

```xml
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-starter-feign</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## 核心组件

| 组件 | 说明 |
| --- | --- |
| `FeignRequestInterceptor` | 自动透传 Token、TraceId、UserId、Username、TenantId 请求头 |
| `FeignResponseDecoder` | 统一响应体解码包装 |
| `FeignErrorDecoder` | 404 抛出 `ServiceNotFoundException`、429 抛出 `RateLimitException`、5xx 抛出 `RemoteCallException` |
| `AbstractFallbackFactory` | 降级工厂模板，子类实现 `doCreate` 即可 |
| `FeignLogConfig` | Feign 请求日志配置 |
| `OkHttpConfig` | OkHttp 客户端自动装配 |

## Feign Client 使用示例

```java
@FeignClient(name = "user-service", fallbackFactory = UserFallbackFactory.class)
public interface UserClient {

    @GetMapping("/user/{id}")
    Result<UserVO> getUser(@PathVariable("id") Long id);
}
```

## 降级工厂示例

```java
@Component
public class UserFallbackFactory extends AbstractFallbackFactory<UserClient> {

    @Override
    protected UserClient doCreate(Throwable cause) {
        return new UserClient() {
            @Override
            public Result<UserVO> getUser(Long id) {
                return Result.fail("用户服务不可用: " + cause.getMessage());
            }
        };
    }
}
```

## 透传的请求头

| 请求头 | 来源 |
| --- | --- |
| `X-Token` | 原始 HTTP 请求 |
| `X-Inner-Call` | 原始 HTTP 请求 |
| `X-Trace-Id` | `TraceContext` |
| `X-User-Id` | `UserContext` |
| `X-Username` | `UserContext` |
| `X-Tenant-Id` | `UserContext` |

## 使用说明

引入依赖后，`FeignRequestInterceptor`、`FeignResponseDecoder`、`FeignErrorDecoder` 自动注册。需在启动类添加 `@EnableFeignClients` 注解。

## FAQ

**Q: 如何关闭 Feign 日志？**
A: 在 YAML 中设置 `logging.level.<feign-client-package>=NONE` 或替换 `FeignLogConfig` Bean。

**Q: 降级工厂如何生效？**
A: 需在 Feign Client 接口的 `@FeignClient` 注解中指定 `fallbackFactory` 属性，且必须在配置中开启 `spring.cloud.openfeign.circuitbreaker.enabled=true`。
