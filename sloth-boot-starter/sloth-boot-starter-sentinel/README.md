# Sloth Boot Starter Sentinel

流量治理组件，集成 Sentinel 限流降级能力，支持 Nacos 动态规则数据源，自动注册全局 Block 异常处理器。

## Maven 依赖

```xml
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-starter-sentinel</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## 配置项

| 配置项 | 类型 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `sloth.sentinel.enabled` | `boolean` | `true` | 是否启用 Sentinel Starter |
| `sloth.sentinel.datasource` | `String` | `nacos` | 规则数据源类型（目前支持 `nacos`） |
| `sloth.sentinel.nacos-group-id` | `String` | `SENTINEL_GROUP` | Nacos 规则分组 |

## 核心组件

| 组件 | 说明 |
| --- | --- |
| `SentinelProperties` | 配置属性 |
| `NacosDataSourceConfig` | 自动注册 Nacos 数据源，监听限流、降级、系统、热点参数规则 |
| `GlobalBlockHandler` | 全局 Block 异常统一处理 |
| `SentinelBlockExceptionHandler` | Web MVC Block 异常处理器 |
| `DefaultFallbackFactory` | 默认降级工厂 |

## Nacos 规则 DataId 命名

| 规则类型 | DataId |
| --- | --- |
| 流控规则 | `{applicationName}-flow-rules` |
| 降级规则 | `{applicationName}-degrade-rules` |
| 系统规则 | `{applicationName}-system-rules` |
| 热点参数规则 | `{applicationName}-param-rules` |

## 配置示例

```yaml
sloth:
  sentinel:
    enabled: true
    datasource: nacos
    nacos-group-id: SENTINEL_GROUP

spring:
  cloud:
    nacos:
      config:
        server-addr: localhost:8848
```

## 使用示例

```java
// 限流资源定义
@SentinelResource(value = "getUser", blockHandler = "getUserBlock")
public UserVO getUser(Long id) {
    return userService.getById(id);
}

public UserVO getUserBlock(Long id, BlockException ex) {
    return UserVO.fallback("系统繁忙，请稍后重试");
}
```

## Nacos 流控规则 JSON 示例

```json
[
  {
    "resource": "getUser",
    "limitApp": "default",
    "grade": 1,
    "count": 100,
    "strategy": 0,
    "controlBehavior": 0
  }
]
```

## FAQ

**Q: 规则如何动态生效？**
A: 在 Nacos 控制台修改对应的 DataId JSON 配置，Sentinel 会自动拉取并生效，无需重启。

**Q: 如何自定义 Block 异常返回？**
A: 替换 `GlobalBlockHandler` Bean，实现自定义 JSON 响应格式。

**Q: 网关场景如何使用？**
A: Gateway 模块已内置 `SentinelFallbackHandler`，当 classpath 中存在 Sentinel 时自动生效。
