# sloth-boot-example-service

`sloth-boot-example-service` 是 Sloth Boot 的单体示例服务，用来演示各个基础 starter 的典型接入方式。

## 模块用途

这个示例工程主要用来说明以下几件事：

- 如何引入 `starter-web`
- 如何接入 `starter-ai`
- 如何接入 `starter-redis`
- 如何接入 `starter-mybatis`
- 如何启用线程池、监控和接口文档
- 如何组织 `application.yml`、`application-dev.yml` 和日志配置

## 运行前准备

1. 准备本地 `MySQL`
2. 准备本地 `Redis`
3. 按需修改 `src/main/resources/application-dev.yml`

## 启动方式

在项目根目录执行：

```bash
mvn -pl sloth-boot-example/sloth-boot-example-service spring-boot:run
```

## 默认访问地址

- 应用健康检查：`GET /health`
- AI 对话示例：`GET /ai/chat?prompt=你好`
- Actuator 健康检查：`GET /actuator/health`
- 接口文档入口：`GET /doc.html`

## 配置文件说明

- `application.yml`
  - 放通用配置示例
  - 展示常见 `sloth.*` 配置项
- `application-dev.yml`
  - 放开发环境数据库和 Redis 配置
- `bootstrap.yml`
  - 提供 Spring Boot 3.5 / SCA 2025.x 下的 Nacos 迁移说明模板，不再作为实际加载入口
- `logback-spring.xml`
  - 提供控制台、文件和生产环境 JSON 风格日志输出配置

## 适合怎么使用

- 如果你只想看 starter 的接入方式，直接从这个模块开始看
- 如果你要验证自己的改动有没有破坏主链路，可以优先用这个模块做冒烟测试
- 如果你准备在 GitHub 展示项目，这个模块也是最适合截图和写使用示例的地方
