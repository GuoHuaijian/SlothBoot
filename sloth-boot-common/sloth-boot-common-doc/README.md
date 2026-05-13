# sloth-boot-common-doc

> SlothBoot API 文档模块，基于 Knife4j + OpenAPI 3 自动配置接口文档。

## Maven 依赖

```xml
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-common-doc</artifactId>
</dependency>
```

## 核心组件

| 组件 | 说明 |
|------|------|
| `DocAutoConfiguration` | 自动配置 OpenAPI 3 文档（Knife4j） |
| `DocProperties` | 文档配置属性（`sloth.doc.*`） |

## 启动后访问

- Knife4j UI: `http://localhost:8080/doc.html`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## 配置说明

```yaml
sloth:
  doc:
    enabled: true                          # 是否启用接口文档
    title: "Sloth Boot API"                # 文档标题
    description: "接口文档"                 # 文档描述
    version: "1.0.0"                       # 版本号
    contact-name: "sloth-boot"             # 联系人
    contact-email: "sloth-boot@example.com"
    contact-url: "https://github.com/GuoHuaijian/SlothBoot"
    base-packages:                         # 扫描包路径
      - com.sloth.boot
    security-scheme-enabled: true          # Bearer Token 安全方案
    security-bearer-format: JWT
    server-url: "http://localhost:8080"    # 服务器地址（可自动检测）
    groups:                                # 多分组配置
      - name: "用户模块"
        paths: ["/api/user/**"]
```

## 注意事项

- 开发环境建议启用，生产环境建议关闭：`sloth.doc.enabled=false`
- `GlobalResponseAdvice` 已自动排除 Swagger/Knife4j 相关路径的响应包装
