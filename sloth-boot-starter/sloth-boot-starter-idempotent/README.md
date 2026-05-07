# Sloth Boot Starter Idempotent

接口幂等组件，提供 `@Idempotent` 注解实现 Token 模式 + Redis 去重，支持 SpEL 动态 Key 解析。

## Maven 依赖

```xml
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-starter-idempotent</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## 配置项

| 配置项 | 类型 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `sloth.idempotent.enabled` | `boolean` | `true` | 是否启用幂等组件 |
| `sloth.idempotent.timeout` | `int` | `10` | 幂等锁默认超时时间（秒） |
| `sloth.idempotent.key-prefix` | `String` | `idempotent:` | Redis Key 前缀 |

## 核心组件

| 组件 | 说明 |
| --- | --- |
| `@Idempotent` | 幂等注解，标注在方法上 |
| `IdempotentAspect` | AOP 切面，基于 Redis `setIfAbsent` 实现防重 |
| `TokenIdempotentService` | Token 模式服务，提供 createToken / checkToken |
| `SpelKeyResolver` | SpEL 表达式 Key 解析器 |
| `IdempotentProperties` | 配置属性 |

## @Idempotent 注解属性

| 属性 | 类型 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `timeout` | `int` | `10` | 幂等锁超时时间（秒），为 0 时使用全局配置 |
| `message` | `String` | `请勿重复操作` | 重复请求时的提示消息 |
| `key` | `String` | `""` | 幂等 Key（SpEL 表达式），为空时默认为 `方法签名:userId` |

## 使用示例

### 注解模式（推荐）

```java
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/order/create")
    @Idempotent(message = "订单创建中，请勿重复提交")
    public Result<Long> createOrder(@RequestBody CreateOrderDTO dto) {
        return Result.ok(orderService.create(dto));
    }

    @PostMapping("/order/pay")
    @Idempotent(key = "#dto.orderNo", timeout = 30, message = "支付处理中")
    public Result<Void> payOrder(@RequestBody PayOrderDTO dto) {
        orderService.pay(dto);
        return Result.ok();
    }
}
```

### Token 模式

```java
@RestController
@RequiredArgsConstructor
public class TokenController {

    private final TokenIdempotentService tokenService;

    @GetMapping("/token/create")
    public Result<String> createToken() {
        return Result.ok(tokenService.createToken());
    }

    @PostMapping("/submit")
    public Result<Void> submit(@RequestParam String token) {
        if (!tokenService.checkToken(token)) {
            return Result.fail("请勿重复提交");
        }
        // 业务逻辑
        return Result.ok();
    }
}
```

## FAQ

**Q: SpEL Key 支持哪些变量？**
A: 支持方法参数名（如 `#dto.orderNo`）、`#root` 等标准 SpEL 表达式。为空时默认使用 `方法签名:当前用户ID`。

**Q: 异常时锁会释放吗？**
A: 会。切面在方法抛异常时会校验并删除自己持有的锁，避免业务失败导致后续请求被拦截。

**Q: 依赖哪些中间件？**
A: 依赖 Redis（通过 `StringRedisTemplate`），需确保项目中已配置 Redis 连接。
