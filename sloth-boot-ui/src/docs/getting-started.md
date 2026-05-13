# 快速开始

## 环境要求

| 工具 | 版本要求 | 说明 |
|------|---------|------|
| JDK | 21+ | 推荐 GraalVM JDK 21 |
| Maven | 3.8.1+ | 构建工具 |
| MySQL | 8.0+ | 示例工程需要 |
| Redis | 6.0+ | 示例工程需要 |
| Node.js | 18+ | 前端开发需要（可选） |

## 1. 克隆仓库

```bash
git clone https://github.com/GuoHuaijian/SlothBoot.git
cd SlothBoot
```

## 2. 构建全量模块

```bash
mvn clean verify
```

首次构建会下载依赖，耗时较长。后续构建会利用本地缓存加速。

## 3. 引入模块

在你的项目 `pom.xml` 中添加需要的 starter：

```xml
<!-- 统一版本管理 -->
<parent>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-parent</artifactId>
    <version>${revision}</version>
</parent>

<!-- 按需引入 -->
<dependencies>
    <!-- Web 基础能力 -->
    <dependency>
        <groupId>com.sloth.boot</groupId>
        <artifactId>sloth-boot-starter-web</artifactId>
    </dependency>

    <!-- Redis 缓存 / 分布式锁 -->
    <dependency>
        <groupId>com.sloth.boot</groupId>
        <artifactId>sloth-boot-starter-redis</artifactId>
    </dependency>

    <!-- MyBatis-Plus 数据层 -->
    <dependency>
        <groupId>com.sloth.boot</groupId>
        <artifactId>sloth-boot-starter-mybatis</artifactId>
    </dependency>
</dependencies>
```

## 4. 配置应用

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your_db
    username: root
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379

sloth:
  web:
    enabled: true
    unified-response: true
    unified-exception: true
  redis:
    enabled: true
    mode: single
    address: 127.0.0.1:6379
```

## 5. 启动应用

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

## 6. 验证运行

```bash
# 健康检查
curl http://localhost:8080/actuator/health

# API 文档（需引入 common-doc）
open http://localhost:8080/doc.html
```

## Docker 一键启动

```bash
# 启动 MySQL + Redis + 示例服务
docker-compose up -d

# 查看日志
docker-compose logs -f sloth-example
```

## 推荐阅读顺序

```
common-core → starter-web → starter-redis → starter-mybatis → example-service
```

- `common-core`：理解统一返回体 `R<T>`、异常体系、上下文传递
- `starter-web`：全局异常处理、参数校验、XSS 过滤
- `starter-redis`：缓存工具、分布式锁、限流
- `starter-mybatis`：自动填充、数据权限、慢 SQL 监控
- `example-service`：完整示例，可直接运行对照

## 模块选择指南

| 场景 | 推荐模块 |
|------|---------|
| RESTful API | starter-web + common-doc |
| 用户认证 | starter-auth (Sa-Token) |
| 缓存/分布式锁 | starter-redis |
| 数据库 ORM | starter-mybatis |
| 消息队列 | starter-mq (RocketMQ) |
| 文件上传 | starter-oss (本地/MinIO/阿里云) |
| Excel 导入导出 | starter-excel |
| 定时任务 | starter-job (XXL-Job) |
| 流量防护 | starter-sentinel |
| 分布式事务 | starter-seata |
| AI 对话 | starter-ai (Spring AI) |
| 系统监控 | starter-monitor |
| 微服务网关 | starter-gateway |
| 远程调用 | starter-feign |
| 搜索引擎 | starter-es (Elasticsearch) |
| 短信发送 | starter-sms |
| 幂等控制 | starter-idempotent |
| 代码生成 | generator |
