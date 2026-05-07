# sloth-boot-starter-auth

基于 [Sa-Token](https://sa-token.cc/) 的认证授权 Starter，提供开箱即用的登录/登出、Token 校验、权限拦截能力。

## 快速接入

```xml
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-starter-auth</artifactId>
</dependency>
```

## 配置项

```yaml
sloth:
  auth:
    enabled: true              # 是否启用（默认 true）
    token-name: Authorization  # Token 请求头名称
    token-timeout: 7200        # Token 有效期（秒）
    active-timeout: -1         # 最低活跃频率（秒），-1 不限
    is-concurrent: true        # 是否允许并发登录
    is-share: true             # 是否共用 Token
    token-prefix: Bearer       # Token 前缀
    white-list:                # 白名单路径（不需要认证）
      - /health
      - /auth/login
      - /doc.html
      - /actuator/**
    black-list:                # 黑名单路径（禁止访问）
      - /admin/forbidden
```

## 核心组件

| 组件 | 说明 |
|------|------|
| `AuthAutoConfiguration` | 自动配置，注册认证拦截器 |
| `SaTokenContextHandler` | 登录后自动同步到 `UserContext`，可重写 `buildUserInfo()` 自定义 |
| `DefaultStpInterface` | 权限/角色查询默认实现，业务侧替换此 Bean |
| `AuthProperties` | 配置属性类 |

## 使用示例

```java
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final SaTokenContextHandler contextHandler;

    @PostMapping("/login")
    public R<String> login(@RequestParam Long userId) {
        StpUtil.login(userId);
        contextHandler.syncToUserContext();
        return R.ok(StpUtil.getTokenValue());
    }

    @PostMapping("/logout")
    public R<Void> logout() {
        StpUtil.logout();
        contextHandler.clearUserContext();
        return R.ok();
    }
}
```

## 注解式鉴权

```java
@SaCheckPermission("user:edit")    // 需要权限
@SaCheckRole("admin")              // 需要角色
@SaCheckLogin                      // 需要登录
```

## 自定义用户信息

业务侧继承 `SaTokenContextHandler` 并重写 `buildUserInfo()`：

```java
@Component
public class MyAuthHandler extends SaTokenContextHandler {

    @Autowired
    private UserService userService;

    @Override
    protected UserContext.UserInfo buildUserInfo() {
        UserContext.UserInfo info = super.buildUserInfo();
        User user = userService.getById(info.getUserId());
        info.setUsername(user.getUsername());
        info.setRoles(user.getRoles());
        info.setTenantId(user.getTenantId());
        return info;
    }
}
```
