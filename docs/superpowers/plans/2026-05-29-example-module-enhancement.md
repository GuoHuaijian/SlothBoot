# 示例模块增强实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 通过包结构重组 + MyBatis ORM 全链路演示 + 生产级注释/Swagger 注解，使示例模块充分展示 SlothBoot 框架能力。

**Architecture:** 按领域分子包（ai/dept/order/product/security/system/user），新增 SysDept + SysUser 实体通过 H2 内存数据库演示 BaseMapperX、LambdaQueryWrapperX、加密类型处理器、JSON 类型处理器、数据权限、批量插入等全部 MyBatis starter 能力。所有 Controller 使用 OpenAPI 3 (@Tag/@Operation/@Parameter) 注解，所有字段/方法使用 Javadoc。

**Tech Stack:** Spring Boot 4.0.6, MyBatis-Plus, H2 Database, Knife4j (OpenAPI 3), Lombok

---

## File Structure

### 新增文件（16 个）
- `src/main/resources/schema.sql` — H2 DDL
- `src/main/resources/data.sql` — 初始数据
- `domain/entity/SysDept.java` — 部门实体
- `domain/entity/SysUser.java` — 用户实体
- `domain/mapper/SysDeptMapper.java` — 部门 Mapper
- `domain/mapper/SysUserMapper.java` — 用户 Mapper
- `model/dept/request/DeptCreateRequest.java` — 部门创建请求
- `model/dept/vo/DeptVO.java` — 部门树 VO
- `model/user/request/UserCreateRequest.java` — 用户创建请求
- `model/user/request/UserQuery.java` — 用户分页查询条件
- `model/user/vo/SysUserVO.java` — 用户脱敏 VO
- `service/dept/DeptService.java` — 部门服务
- `service/user/UserService.java` — 用户服务
- `controller/dept/DeptController.java` — 部门接口
- `controller/user/UserController.java` — 用户接口

### 修改文件
- `pom.xml` — 新增 H2 依赖
- `application.yml` — H2 数据源 + MyBatis 启用 + encrypt-key
- 现有 6 个 Controller — 迁移包路径 + 新增 Swagger 注解
- 现有 6 个 Service — 迁移包路径 + 更新 import
- 现有 12 个 DTO — 迁移到 model/{domain}/ 子包

### 删除文件
- 旧 `controller/*.java`（迁移后删除）
- 旧 `dto/*.java`（迁移后删除）
- 旧 `service/*.java`（迁移后删除）

---

## Task 1: 构建配置 + H2 数据源

**Files:**
- Modify: `sloth-boot-example/sloth-boot-example-service/pom.xml`
- Modify: `sloth-boot-example/sloth-boot-example-service/src/main/resources/application.yml`

- [ ] **Step 1: pom.xml 新增 H2 依赖**

在 `<dependencies>` 的 `mysql-connector-j` 之后添加：

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

- [ ] **Step 2: application.yml 替换数据源配置**

将 `spring.datasource` 部分替换为 H2：

```yaml
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:sloth_boot;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=FALSE
    username: sa
    password:
  sql:
    init:
      mode: always
      schema-locations: classpath:schema.sql
      data-locations: classpath:data.sql
  h2:
    console:
      enabled: true
      path: /h2-console
```

- [ ] **Step 3: 移除 MyBatis/DataSource 的 autoconfigure exclude**

从 `spring.autoconfigure.exclude` 列表中删除以下三项：
- `com.sloth.boot.starter.mybatis.config.MybatisPlusAutoConfiguration`
- `org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration`
- `org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration`

- [ ] **Step 4: 新增 encrypt-key 配置**

在 `sloth.mybatis` 配置块中添加：

```yaml
sloth:
  mybatis:
    encrypt-key: sloth-boot-example-key-2024
```

（替换原有的 `sloth.mybatis` 块为以下完整配置）

```yaml
  mybatis:
    enabled: true
    tenant-enabled: false
    tenant-column: tenant_id
    tenant-ignore-tables:
      - sys_dict
    slow-sql-threshold: 1000
    encrypt-key: sloth-boot-example-key-2024
```

- [ ] **Step 5: 更新 doc.base-packages**

将 `sloth.doc.base-packages` 更新为新包路径（迁移后生效）：

```yaml
  doc:
    enabled: true
    title: "示例服务 API"
    description: "Sloth Boot 示例服务接口文档"
    version: "1.0.0"
    contact-name: "sloth-boot"
    contact-email: "564559079@qq.com"
    base-packages:
      - com.sloth.boot.example.controller
```

（保持不变，因为 controller 包仍叫 controller，只是内部有了子包）

---

## Task 2: 数据库初始化脚本

**Files:**
- Create: `sloth-boot-example/sloth-boot-example-service/src/main/resources/schema.sql`
- Create: `sloth-boot-example/sloth-boot-example-service/src/main/resources/data.sql`

- [ ] **Step 1: 创建 schema.sql**

```sql
-- ============================================================
-- Sloth Boot 示例 - H2 数据库初始化（兼容 MySQL 语法）
-- ============================================================

-- 部门表：演示 TreeUtil 树结构、数据权限、自动填充
CREATE TABLE IF NOT EXISTS sys_dept (
    id          BIGINT       PRIMARY KEY COMMENT '部门ID（雪花算法生成）',
    name        VARCHAR(64)  NOT NULL    COMMENT '部门名称',
    parent_id   BIGINT       DEFAULT 0   COMMENT '父部门ID（0表示顶级）',
    sort        INT          DEFAULT 0   COMMENT '显示排序',
    leader      VARCHAR(64)              COMMENT '负责人',
    status      TINYINT      DEFAULT 0   COMMENT '状态（0-正常, 1-停用）',
    ancestors   VARCHAR(512) DEFAULT ''  COMMENT '祖级列表（逗号分隔，如 0,1,2）',
    create_by   VARCHAR(64)              COMMENT '创建人（自动填充）',
    create_time TIMESTAMP                COMMENT '创建时间（自动填充）',
    update_by   VARCHAR(64)              COMMENT '更新人（自动填充）',
    update_time TIMESTAMP                COMMENT '更新时间（自动填充）',
    deleted     TINYINT      DEFAULT 0   COMMENT '逻辑删除标记（0-正常, 1-已删除）',
    version     INT          DEFAULT 1   COMMENT '乐观锁版本号'
);

-- 用户表：演示 EncryptTypeHandler、JsonTypeHandler、数据权限
CREATE TABLE IF NOT EXISTS sys_user (
    id          BIGINT       PRIMARY KEY COMMENT '用户ID（雪花算法生成）',
    dept_id     BIGINT                   COMMENT '所属部门ID',
    username    VARCHAR(64)  NOT NULL    COMMENT '用户名',
    phone       VARCHAR(128)             COMMENT '手机号（AES 加密存储）',
    id_card     VARCHAR(128)             COMMENT '身份证号（AES 加密存储）',
    email       VARCHAR(128)             COMMENT '邮箱',
    gender      TINYINT      DEFAULT 0   COMMENT '性别（0-未知, 1-男, 2-女）',
    status      TINYINT      DEFAULT 0   COMMENT '状态（0-正常, 1-停用）',
    extra_info  TEXT                     COMMENT '扩展信息（JSON 格式存储）',
    create_by   VARCHAR(64)              COMMENT '创建人（自动填充）',
    create_time TIMESTAMP                COMMENT '创建时间（自动填充）',
    update_by   VARCHAR(64)              COMMENT '更新人（自动填充）',
    update_time TIMESTAMP                COMMENT '更新时间（自动填充）',
    deleted     TINYINT      DEFAULT 0   COMMENT '逻辑删除标记（0-正常, 1-已删除）',
    version     INT          DEFAULT 1   COMMENT '乐观锁版本号'
);
```

- [ ] **Step 2: 创建 data.sql**

```sql
-- ============================================================
-- 初始数据
-- ============================================================

-- 部门初始数据
INSERT INTO sys_dept (id, name, parent_id, sort, leader, status, ancestors, create_by, create_time, update_by, update_time, deleted, version)
VALUES
    (1, 'Sloth科技',     0, 1, '管理员', 0, '0',      'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 0, 1),
    (2, '技术研发部',     1, 1, '张三',   0, '0,1',    'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 0, 1),
    (3, '产品设计部',     1, 2, '李四',   0, '0,1',    'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 0, 1),
    (4, '后端开发组',     2, 1, '王五',   0, '0,1,2',  'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 0, 1),
    (5, '前端开发组',     2, 2, '赵六',   0, '0,1,2',  'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 0, 1);

-- 用户初始数据
-- phone 和 id_card 使用 encrypt-key = "sloth-boot-example-key-2024" 加密后的值
-- 注意：H2 初始化时 EncryptTypeHandler 可能尚未就绪，此处存储明文作为演示
-- 实际加密存储通过 API 创建用户时生效
INSERT INTO sys_user (id, dept_id, username, phone, id_card, email, gender, status, extra_info, create_by, create_time, update_by, update_time, deleted, version)
VALUES
    (1, 1, 'admin',     '13800138000', '110101199001011234', 'admin@sloth.boot',     1, 0, '{"role":"super_admin","tags":["admin"]}',     'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 0, 1),
    (2, 4, 'dev_user',  '13900139000', '110101199002021234', 'dev@sloth.boot',       1, 0, '{"role":"developer","tags":["backend"]}',     'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 0, 1),
    (3, 3, 'prod_user', '13700137000', '110101199003031234', 'product@sloth.boot',   2, 0, '{"role":"product_mgr","tags":["product"]}',   'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 0, 1);
```

---

## Task 3: 现有文件包迁移 + Swagger 注解

**说明：** 将现有 25 个 Java 文件迁移到新的按领域分包结构，同时为所有 Controller 添加 OpenAPI 3 注解。迁移后删除旧文件。

**Files:**
- Create: `controller/ai/AiController.java` (从 `controller/AiController.java` 迁移)
- Create: `controller/monitor/MonitorController.java` (从 `controller/MonitorController.java` 迁移)
- Create: `controller/order/OrderController.java` (从 `controller/OrderController.java` 迁移)
- Create: `controller/product/ProductController.java` (从 `controller/ProductController.java` 迁移)
- Create: `controller/security/SecurityController.java` (从 `controller/SecurityController.java` 迁移)
- Create: `controller/system/SystemController.java` (从 `controller/SystemController.java` 迁移)
- Create: `service/ai/AiDemoService.java` (从 `service/AiDemoService.java` 迁移)
- Create: `service/monitor/MonitorDemoService.java` (从 `service/MonitorDemoService.java` 迁移)
- Create: `service/order/OrderDemoService.java` (从 `service/OrderDemoService.java` 迁移)
- Create: `service/product/ProductDemoService.java` (从 `service/ProductDemoService.java` 迁移)
- Create: `service/security/SecurityDemoService.java` (从 `service/SecurityDemoService.java` 迁移)
- Create: `service/system/SystemDemoService.java` (从 `service/SystemDemoService.java` 迁移)
- Create: `model/order/request/OrderCreateRequest.java` (从 `dto/OrderCreateRequest.java` 迁移)
- Create: `model/order/dto/OrderDTO.java` (从 `dto/OrderDTO.java` 迁移)
- Create: `model/order/event/OrderStatusEvent.java` (从 `dto/OrderStatusEvent.java` 迁移)
- Create: `model/product/request/ProductCreateRequest.java` (从 `dto/ProductCreateRequest.java` 迁移)
- Create: `model/product/dto/ProductDTO.java` (从 `dto/ProductDTO.java` 迁移)
- Create: `model/security/request/CryptoRequest.java` (从 `dto/CryptoRequest.java` 迁移)
- Create: `model/security/vo/CryptoResponse.java` (从 `dto/CryptoResponse.java` 迁移)
- Create: `model/system/request/LoginRequest.java` (从 `dto/LoginRequest.java` 迁移)
- Create: `model/system/vo/LoginResponse.java` (从 `dto/LoginResponse.java` 迁移)
- Create: `model/system/vo/SystemUserVO.java` (从 `dto/UserVO.java` 迁移并重命名)
- Create: `model/monitor/vo/JvmInfo.java` (从 `dto/JvmInfo.java` 迁移)
- Create: `model/monitor/vo/MetricSummary.java` (从 `dto/MetricSummary.java` 迁移)
- Delete: `controller/*.java` (6 files)
- Delete: `dto/*.java` (12 files)
- Delete: `service/*.java` (6 files)

- [ ] **Step 1: 迁移 OrderController 到 controller/order/ 并添加 Swagger 注解**

创建 `controller/order/OrderController.java`，完整内容：

```java
package com.sloth.boot.example.controller.order;

import cn.dev33.satoken.stp.StpUtil;
import com.sloth.boot.common.log.annotation.OperateLog;
import com.sloth.boot.common.log.annotation.OperateTypeEnum;
import com.sloth.boot.common.result.R;
import com.sloth.boot.example.model.order.dto.OrderDTO;
import com.sloth.boot.example.model.order.event.OrderStatusEvent;
import com.sloth.boot.example.model.order.request.OrderCreateRequest;
import com.sloth.boot.example.service.order.OrderDemoService;
import com.sloth.boot.starter.idempotent.annotation.Idempotent;
import com.sloth.boot.starter.redis.annotation.DistributedLock;
import com.sloth.boot.starter.redis.annotation.RateLimit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单演示接口
 * <p>
 * 演示能力：分布式锁 (@DistributedLock)、幂等 (@Idempotent)、限流 (@RateLimit)、
 * 操作日志 (@OperateLog)、Redis Pub/Sub 事件发布
 */
@Tag(name = "订单管理", description = "演示分布式锁、幂等、限流、操作日志、Redis Pub/Sub 等能力")
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderDemoService orderService;

    /**
     * 创建订单
     * <p>
     * 演示：@DistributedLock 分布式锁防并发 + @Idempotent 幂等防重
     */
    @Operation(summary = "创建订单", description = "下单时使用分布式锁防止并发超卖，幂等注解防止重复提交")
    @DistributedLock(key = "'order:create:' + #request.productId", waitTime = 5, leaseTime = 30, message = "下单处理中，请勿重复操作")
    @Idempotent(timeout = 30, message = "请勿重复提交订单")
    @PostMapping("/create")
    public R<OrderDTO> createOrder(@RequestBody OrderCreateRequest request) {
        long userId = StpUtil.isLogin() ? StpUtil.getLoginIdAsLong() : 1L;
        return R.ok(orderService.createOrder(request, userId));
    }

    /**
     * 查询订单详情
     */
    @Operation(summary = "查询订单", description = "根据订单ID查询订单详情")
    @Parameter(name = "id", description = "订单ID", required = true, example = "1")
    @GetMapping("/{id}")
    public R<OrderDTO> getOrder(@PathVariable Long id) {
        OrderDTO order = orderService.getOrder(id);
        if (order == null) {
            return R.fail("订单不存在");
        }
        return R.ok(order);
    }

    /**
     * 支付订单
     * <p>
     * 演示：@DistributedLock 保证支付操作的原子性
     */
    @Operation(summary = "支付订单", description = "使用分布式锁保证同一订单支付操作串行执行")
    @Parameter(name = "id", description = "订单ID", required = true)
    @DistributedLock(key = "'order:' + #id", waitTime = 5, leaseTime = 30, message = "支付处理中")
    @PutMapping("/{id}/pay")
    public R<OrderDTO> payOrder(@PathVariable Long id) {
        return R.ok(orderService.payOrder(id));
    }

    /**
     * 取消订单
     * <p>
     * 演示：@OperateLog 操作日志记录
     */
    @Operation(summary = "取消订单", description = "取消订单并记录操作日志")
    @Parameter(name = "id", description = "订单ID", required = true)
    @OperateLog(module = "订单管理", description = "取消订单", type = OperateTypeEnum.UPDATE)
    @PutMapping("/{id}/cancel")
    public R<OrderDTO> cancelOrder(@PathVariable Long id) {
        return R.ok(orderService.cancelOrder(id));
    }

    /**
     * 查询订单列表
     * <p>
     * 演示：@OperateLog 查询操作日志
     */
    @Operation(summary = "查询订单列表", description = "查询全部订单，按创建时间倒序")
    @OperateLog(module = "订单管理", description = "查询订单列表", type = OperateTypeEnum.QUERY)
    @GetMapping("/list")
    public R<List<OrderDTO>> listOrders() {
        return R.ok(orderService.listOrders());
    }

    /**
     * 限流测试
     * <p>
     * 演示：@RateLimit 滑动窗口限流（10秒内最多5次请求）
     */
    @Operation(summary = "限流测试", description = "10秒内最多5次请求，超出返回限流提示")
    @GetMapping("/rate-limit-test")
    @RateLimit(count = 5, period = 10, message = "10秒内最多5次请求")
    public R<String> rateLimitTest() {
        return R.ok("请求成功");
    }

    /**
     * 获取最近的订单事件
     * <p>
     * 演示：Redis Pub/Sub 事件订阅与接收
     */
    @Operation(summary = "获取订单事件", description = "获取通过 Redis Pub/Sub 接收到的最近订单状态变更事件")
    @Parameter(name = "count", description = "返回事件数量", example = "20")
    @GetMapping("/events")
    public R<List<OrderStatusEvent>> getRecentEvents(@RequestParam(defaultValue = "20") int count) {
        return R.ok(orderService.getRecentEvents(count));
    }
}
```

- [ ] **Step 2: 迁移 ProductController 到 controller/product/ 并添加 Swagger 注解**

创建 `controller/product/ProductController.java`，完整内容：

```java
package com.sloth.boot.example.controller.product;

import com.sloth.boot.common.log.annotation.OperateLog;
import com.sloth.boot.common.log.annotation.OperateTypeEnum;
import com.sloth.boot.common.result.R;
import com.sloth.boot.example.model.product.dto.ProductDTO;
import com.sloth.boot.example.model.product.request.ProductCreateRequest;
import com.sloth.boot.example.service.product.ProductDemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商品演示接口
 * <p>
 * 演示能力：操作日志、布隆过滤器、逻辑过期缓存、ZSet 排行榜、XSS 防护
 */
@Tag(name = "商品管理", description = "演示布隆过滤器、缓存策略、ZSet排行榜、XSS防护等能力")
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductDemoService productService;

    /**
     * 查询商品详情
     * <p>
     * 演示：布隆过滤器拦截不存在的 key，防止缓存穿透
     */
    @Operation(summary = "查询商品", description = "布隆过滤器拦截不存在的请求，防止缓存穿透")
    @Parameter(name = "id", description = "商品ID", required = true, example = "1")
    @GetMapping("/{id}")
    public R<ProductDTO> getProduct(@PathVariable Long id) {
        ProductDTO product = productService.getProduct(id);
        if (product == null) {
            return R.fail("商品不存在（布隆过滤器拦截或缓存未命中）");
        }
        return R.ok(product);
    }

    /**
     * 查询商品列表
     */
    @Operation(summary = "查询商品列表", description = "查询全部商品列表")
    @OperateLog(module = "商品管理", description = "查询商品列表", type = OperateTypeEnum.QUERY)
    @GetMapping("/list")
    public R<List<ProductDTO>> listProducts() {
        return R.ok(productService.listProducts());
    }

    /**
     * 创建商品
     * <p>
     * 演示：XSS 防护（输入内容自动清洗）+ @OperateLog 操作审计
     */
    @Operation(summary = "创建商品", description = "创建商品，输入内容自动进行 XSS 清洗")
    @OperateLog(module = "商品管理", description = "创建商品", type = OperateTypeEnum.CREATE)
    @PostMapping
    public R<ProductDTO> createProduct(@RequestBody ProductCreateRequest request) {
        return R.ok(productService.createProduct(request));
    }

    /**
     * 更新商品
     */
    @Operation(summary = "更新商品", description = "更新商品信息，输入内容自动进行 XSS 清洗")
    @Parameter(name = "id", description = "商品ID", required = true)
    @OperateLog(module = "商品管理", description = "更新商品", type = OperateTypeEnum.UPDATE)
    @PutMapping("/{id}")
    public R<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductCreateRequest request) {
        return R.ok(productService.updateProduct(id, request));
    }

    /**
     * 删除商品
     */
    @Operation(summary = "删除商品", description = "删除指定商品")
    @Parameter(name = "id", description = "商品ID", required = true)
    @OperateLog(module = "商品管理", description = "删除商品", type = OperateTypeEnum.DELETE)
    @DeleteMapping("/{id}")
    public R<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return R.ok("删除成功");
    }

    /**
     * 获取商品排行榜
     * <p>
     * 演示：Redis ZSet 实现排行榜
     */
    @Operation(summary = "商品排行榜", description = "基于 Redis ZSet 的商品投票排行榜")
    @GetMapping("/rank")
    public R<?> getRank() {
        return R.ok(productService.getRank());
    }

    /**
     * 商品投票
     * <p>
     * 演示：Redis ZSet ZINCRBY 实时计分
     */
    @Operation(summary = "商品投票", description = "为指定商品投票，使用 Redis ZSet 实时更新排名")
    @Parameter(name = "productId", description = "商品ID", required = true)
    @PostMapping("/rank/vote")
    public R<String> voteProduct(@RequestParam Long productId) {
        productService.voteProduct(productId);
        return R.ok("投票成功");
    }

    /**
     * 缓存策略演示
     * <p>
     * 演示：三种缓存策略对比 — 基础缓存、缓存击穿防护（getOrLoad）、逻辑过期
     */
    @Operation(summary = "缓存策略演示", description = "对比演示三种缓存策略：基础缓存、缓存击穿防护、逻辑过期")
    @GetMapping("/cache/demo")
    public R<Map<String, Object>> demoCacheStrategies() {
        return R.ok(productService.demoCacheStrategies());
    }

    /**
     * 布隆过滤器统计
     */
    @Operation(summary = "布隆过滤器统计", description = "查看布隆过滤器的插入数量和误判率配置")
    @GetMapping("/bloom/stats")
    public R<Map<String, Object>> getBloomStats() {
        return R.ok(productService.getBloomStats());
    }

    /**
     * 重置布隆过滤器
     */
    @Operation(summary = "重置布隆过滤器", description = "清空并重建布隆过滤器")
    @PostMapping("/bloom/reset")
    public R<String> resetBloom() {
        productService.resetBloom();
        return R.ok("布隆过滤器已重置");
    }
}
```

- [ ] **Step 3: 迁移其余 4 个 Controller（Ai/Monitor/Security/System）到对应子包并添加 Swagger 注解**

对每个 Controller 执行相同模式：
1. 创建新路径文件，更新 package 声明
2. 更新 import 路径（`dto.*` → `model.{domain}.*`）
3. 在 SystemController 中将 `UserVO` 重命名为 `SystemUserVO` 的 import
4. 添加 `@Tag` 类注解
5. 添加 `@Operation` 方法注解
6. 添加 `@Parameter` 参数注解

**SystemController 关键变更**（其余类似）：

```java
package com.sloth.boot.example.controller.system;

// ... imports (更新 dto → model.system)
import com.sloth.boot.example.model.system.vo.SystemUserVO;
import com.sloth.boot.example.model.system.vo.LoginResponse;
import com.sloth.boot.example.model.system.request.LoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "系统管理", description = "演示 Sa-Token 认证授权、数据脱敏、数据权限等能力")
@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
public class SystemController {
    // ... 方法上添加 @Operation 注解
    // ... 返回类型 UserVO 改为 SystemUserVO
}
```

- [ ] **Step 4: 迁移 6 个 Service 到对应子包**

对每个 Service 执行：
1. 创建新路径文件（如 `service/order/OrderDemoService.java`）
2. 更新 package 声明
3. 更新所有 import（`dto.*` → `model.{domain}.*`）

Service 层不加 Swagger 注解（Swagger 注解仅用于 Controller 层）。

- [ ] **Step 5: 迁移 12 个 DTO 到 model/{domain}/ 子包**

迁移映射：

| 原文件 | 新文件 | 新 package |
|--------|--------|-----------|
| `dto/OrderCreateRequest.java` | `model/order/request/OrderCreateRequest.java` | `com.sloth.boot.example.model.order.request` |
| `dto/OrderDTO.java` | `model/order/dto/OrderDTO.java` | `com.sloth.boot.example.model.order.dto` |
| `dto/OrderStatusEvent.java` | `model/order/event/OrderStatusEvent.java` | `com.sloth.boot.example.model.order.event` |
| `dto/ProductCreateRequest.java` | `model/product/request/ProductCreateRequest.java` | `com.sloth.boot.example.model.product.request` |
| `dto/ProductDTO.java` | `model/product/dto/ProductDTO.java` | `com.sloth.boot.example.model.product.dto` |
| `dto/CryptoRequest.java` | `model/security/request/CryptoRequest.java` | `com.sloth.boot.example.model.security.request` |
| `dto/CryptoResponse.java` | `model/security/vo/CryptoResponse.java` | `com.sloth.boot.example.model.security.vo` |
| `dto/LoginRequest.java` | `model/system/request/LoginRequest.java` | `com.sloth.boot.example.model.system.request` |
| `dto/LoginResponse.java` | `model/system/vo/LoginResponse.java` | `com.sloth.boot.example.model.system.vo` |
| `dto/UserVO.java` | `model/system/vo/SystemUserVO.java` | `com.sloth.boot.example.model.system.vo` |
| `dto/JvmInfo.java` | `model/monitor/vo/JvmInfo.java` | `com.sloth.boot.example.model.monitor.vo` |
| `dto/MetricSummary.java` | `model/monitor/vo/MetricSummary.java` | `com.sloth.boot.example.model.monitor.vo` |

每个文件：更新 package 声明，`UserVO` 重命名为 `SystemUserVO`（类名 + 文件名）。

- [ ] **Step 6: 删除旧文件**

删除旧的 `controller/`、`dto/`、`service/` 目录下的所有文件（已迁移到新位置）。

- [ ] **Step 7: 编译验证**

Run: `cd D:/Guo/IDEA/project/SlothBoot && mvn compile -pl sloth-boot-example/sloth-boot-example-service -am -q`
Expected: BUILD SUCCESS（无编译错误）

---

## Task 4: SysDept 实体 + Mapper

**Files:**
- Create: `domain/entity/SysDept.java`
- Create: `domain/mapper/SysDeptMapper.java`

- [ ] **Step 1: 创建 SysDept 实体**

创建 `domain/entity/SysDept.java`：

```java
package com.sloth.boot.example.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sloth.boot.starter.mybatis.core.BaseEntity;
import com.sloth.boot.starter.web.validator.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门实体
 * <p>
 * 演示 MyBatis-Plus 基础能力：雪花ID、自动填充、逻辑删除、乐观锁。
 * 配合 {@link com.sloth.boot.common.base.TreeNode} 和
 * {@link com.sloth.boot.common.util.TreeUtil} 实现部门树结构。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
@Schema(description = "部门实体")
public class SysDept extends BaseEntity {

    /**
     * 部门名称
     */
    @TableField("name")
    @Schema(description = "部门名称", example = "技术研发部", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * 父部门ID（0 表示顶级部门）
     */
    @TableField("parent_id")
    @Schema(description = "父部门ID，0表示顶级", example = "1")
    private Long parentId;

    /**
     * 显示排序
     */
    @TableField("sort")
    @Schema(description = "显示排序", example = "1")
    private Integer sort;

    /**
     * 负责人
     */
    @TableField("leader")
    @Schema(description = "负责人", example = "张三")
    private String leader;

    /**
     * 状态（0-正常, 1-停用）
     */
    @TableField("status")
    @EnumValue(intValues = {0, 1})
    @Schema(description = "状态（0-正常, 1-停用）", example = "0")
    private Integer status;

    /**
     * 祖级列表（逗号分隔，如 0,1,2）
     */
    @TableField("ancestors")
    @Schema(description = "祖级列表", example = "0,1")
    private String ancestors;
}
```

- [ ] **Step 2: 创建 SysDeptMapper**

创建 `domain/mapper/SysDeptMapper.java`：

```java
package com.sloth.boot.example.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.sloth.boot.example.domain.entity.SysDept;
import com.sloth.boot.starter.mybatis.annotation.DataScope;
import com.sloth.boot.starter.mybatis.core.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 部门 Mapper
 * <p>
 * 演示能力：
 * <ul>
 *   <li>{@link BaseMapperX} — 扩展分页查询、按字段查询</li>
 *   <li>{@code insertBatch} — 由 InsertBatchSqlInjector 自动注入的单语句批量插入</li>
 *   <li>{@link DataScope} — 传统数据权限，根据 UserContext.getDataScope() 自动追加 WHERE 条件</li>
 * </ul>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Mapper
public interface SysDeptMapper extends BaseMapperX<SysDept> {

    /**
     * 批量插入部门（单语句，由 InsertBatchSqlInjector 注入）
     *
     * @param list 部门列表
     * @return 插入行数
     */
    int insertBatch(@Param("list") List<SysDept> list);

    /**
     * 带数据权限的部门列表查询
     * <p>
     * 当前用户的 dataScope 为 "dept" 时，仅返回本部门数据；
     * 为 "self" 时，仅返回自己创建的数据；为 "all" 时返回全部。
     *
     * @param wrapper 查询条件
     * @return 部门列表
     */
    @DataScope(deptAlias = "d")
    List<SysDept> selectListWithScope(@Param("ew") Wrapper<SysDept> wrapper);
}
```

---

## Task 5: SysUser 实体 + Mapper

**Files:**
- Create: `domain/entity/SysUser.java`
- Create: `domain/mapper/SysUserMapper.java`

- [ ] **Step 1: 创建 SysUser 实体**

创建 `domain/entity/SysUser.java`：

```java
package com.sloth.boot.example.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sloth.boot.starter.mybatis.core.BaseEntity;
import com.sloth.boot.starter.mybatis.handler.EncryptTypeHandler;
import com.sloth.boot.starter.mybatis.handler.JsonTypeHandler;
import com.sloth.boot.starter.web.validator.EnumValue;
import com.sloth.boot.starter.web.validator.IdCard;
import com.sloth.boot.starter.web.validator.Phone;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 用户实体
 * <p>
 * 演示 MyBatis-Plus 高级能力：
 * <ul>
 *   <li>{@link EncryptTypeHandler} — phone/idCard 字段 AES 加密存储，读取时自动解密</li>
 *   <li>{@link JsonTypeHandler} — extraInfo 以 JSON 字符串存储在 TEXT 列，读取时自动反序列化</li>
 *   <li>{@link Phone} / {@link IdCard} / {@link EnumValue} — JSR-380 自定义校验</li>
 * </ul>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
@Schema(description = "用户实体")
public class SysUser extends BaseEntity {

    /**
     * 所属部门ID
     */
    @TableField("dept_id")
    @Schema(description = "所属部门ID", example = "2")
    private Long deptId;

    /**
     * 用户名
     */
    @TableField("username")
    @Schema(description = "用户名", example = "zhangsan", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    /**
     * 手机号（AES 加密存储）
     */
    @TableField(value = "phone", typeHandler = EncryptTypeHandler.class)
    @Phone
    @Schema(description = "手机号（数据库中AES加密存储，查询时自动解密）", example = "13800138000")
    private String phone;

    /**
     * 身份证号（AES 加密存储）
     */
    @TableField(value = "id_card", typeHandler = EncryptTypeHandler.class)
    @IdCard
    @Schema(description = "身份证号（数据库中AES加密存储，查询时自动解密）", example = "110101199001011234")
    private String idCard;

    /**
     * 邮箱
     */
    @TableField("email")
    @Schema(description = "邮箱", example = "zhangsan@sloth.boot")
    private String email;

    /**
     * 性别（0-未知, 1-男, 2-女）
     */
    @TableField("gender")
    @EnumValue(intValues = {0, 1, 2})
    @Schema(description = "性别（0-未知, 1-男, 2-女）", example = "1")
    private Integer gender;

    /**
     * 状态（0-正常, 1-停用）
     */
    @TableField("status")
    @EnumValue(intValues = {0, 1})
    @Schema(description = "状态（0-正常, 1-停用）", example = "0")
    private Integer status;

    /**
     * 扩展信息（JSON 格式存储）
     * <p>
     * 数据库中以 TEXT 类型存储 JSON 字符串，读取时通过 {@link JsonTypeHandler} 自动反序列化为 Map。
     */
    @TableField(value = "extra_info", typeHandler = JsonTypeHandler.class)
    @Schema(description = "扩展信息（JSON格式存储，自动序列化/反序列化）")
    private Map<String, Object> extraInfo;
}
```

- [ ] **Step 2: 创建 SysUserMapper**

创建 `domain/mapper/SysUserMapper.java`：

```java
package com.sloth.boot.example.domain.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sloth.boot.example.domain.entity.SysUser;
import com.sloth.boot.starter.mybatis.annotation.DataPermission;
import com.sloth.boot.starter.mybatis.core.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户 Mapper
 * <p>
 * 演示能力：
 * <ul>
 *   <li>{@link BaseMapperX} — 分页查询 selectPage(BaseQuery, Wrapper)</li>
 *   <li>{@code insertBatch} — 单语句批量插入</li>
 *   <li>{@link DataPermission} — 增强型数据权限（支持 SpEL 表达式）</li>
 * </ul>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Mapper
public interface SysUserMapper extends BaseMapperX<SysUser> {

    /**
     * 批量插入用户（单语句，由 InsertBatchSqlInjector 注入）
     *
     * @param list 用户列表
     * @return 插入行数
     */
    int insertBatch(@Param("list") List<SysUser> list);

    /**
     * 带数据权限的分页查询
     * <p>
     * 通过 @DataPermission 注解，根据当前用户的数据权限范围自动追加 WHERE 条件。
     * 支持的传统范围：all（全部）、dept（本部门）、dept_and_below（本部门及下级）、self（仅本人）。
     *
     * @param page    分页参数
     * @param wrapper 查询条件
     * @return 分页结果
     */
    @DataPermission(deptAlias = "u", userAlias = "u")
    Page<SysUser> selectPageWithPermission(Page<SysUser> page, @Param("ew") com.baomidou.mybatisplus.core.conditions.Wrapper<SysUser> wrapper);
}
```

---

## Task 6: 请求/响应对象

**Files:**
- Create: `model/dept/request/DeptCreateRequest.java`
- Create: `model/dept/vo/DeptVO.java`
- Create: `model/user/request/UserCreateRequest.java`
- Create: `model/user/request/UserQuery.java`
- Create: `model/user/vo/SysUserVO.java`

- [ ] **Step 1: 创建 DeptCreateRequest**

```java
package com.sloth.boot.example.model.dept.request;

import com.sloth.boot.starter.web.validator.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 部门创建/更新请求
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Schema(description = "部门创建请求")
public class DeptCreateRequest {

    /**
     * 部门名称
     */
    @NotBlank(message = "部门名称不能为空")
    @Schema(description = "部门名称", example = "后端开发组", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * 父部门ID
     */
    @Schema(description = "父部门ID，不传或为0表示顶级", example = "2")
    private Long parentId;

    /**
     * 负责人
     */
    @Schema(description = "负责人", example = "王五")
    private String leader;

    /**
     * 显示排序
     */
    @Schema(description = "显示排序", example = "1")
    private Integer sort;

    /**
     * 状态（0-正常, 1-停用）
     */
    @EnumValue(intValues = {0, 1})
    @Schema(description = "状态（0-正常, 1-停用）", example = "0")
    private Integer status;
}
```

- [ ] **Step 2: 创建 DeptVO**

```java
package com.sloth.boot.example.model.dept.vo;

import com.sloth.boot.common.base.TreeNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 部门视图对象
 * <p>
 * 继承 {@link TreeNode}，支持通过 {@link com.sloth.boot.common.util.TreeUtil#buildTree} 构建部门树。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "部门视图对象（支持树结构）")
public class DeptVO extends TreeNode {

    // id, parentId, sort, children 均继承自 TreeNode

    /**
     * 部门名称
     */
    @Schema(description = "部门名称", example = "技术研发部")
    private String name;

    /**
     * 负责人
     */
    @Schema(description = "负责人", example = "张三")
    private String leader;

    /**
     * 状态
     */
    @Schema(description = "状态（0-正常, 1-停用）", example = "0")
    private Integer status;

    /**
     * 祖级列表
     */
    @Schema(description = "祖级列表", example = "0,1")
    private String ancestors;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createBy;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 子部门列表（覆盖 TreeNode.children 为 List&lt;DeptVO&gt;，确保 JSON 序列化保留完整字段）
     */
    @Schema(description = "子部门列表")
    private List<DeptVO> children;
}
```

- [ ] **Step 3: 创建 UserCreateRequest**

```java
package com.sloth.boot.example.model.user.request;

import com.sloth.boot.starter.web.validator.EnumValue;
import com.sloth.boot.starter.web.validator.IdCard;
import com.sloth.boot.starter.web.validator.Phone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

/**
 * 用户创建请求
 * <p>
 * 演示自定义校验注解：@Phone、@IdCard、@EnumValue
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Schema(description = "用户创建请求")
public class UserCreateRequest {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名", example = "zhangsan", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    /**
     * 手机号（通过 @Phone 校验格式）
     */
    @Phone
    @Schema(description = "手机号（@Phone 格式校验）", example = "13800138000")
    private String phone;

    /**
     * 身份证号（通过 @IdCard 校验格式）
     */
    @IdCard
    @Schema(description = "身份证号（@IdCard 格式校验）", example = "110101199001011234")
    private String idCard;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱", example = "zhangsan@sloth.boot")
    private String email;

    /**
     * 性别（0-未知, 1-男, 2-女）
     */
    @EnumValue(intValues = {0, 1, 2})
    @Schema(description = "性别（0-未知, 1-男, 2-女）", example = "1")
    private Integer gender;

    /**
     * 状态（0-正常, 1-停用）
     */
    @EnumValue(intValues = {0, 1})
    @Schema(description = "状态（0-正常, 1-停用）", example = "0")
    private Integer status;

    /**
     * 所属部门ID
     */
    @Schema(description = "所属部门ID", example = "2")
    private Long deptId;

    /**
     * 扩展信息（JSON 格式）
     */
    @Schema(description = "扩展信息（JSON对象，通过JsonTypeHandler自动存储为JSON字符串）")
    private Map<String, Object> extraInfo;
}
```

- [ ] **Step 4: 创建 UserQuery**

```java
package com.sloth.boot.example.model.user.request;

import com.sloth.boot.common.base.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户分页查询条件
 * <p>
 * 继承 {@link BaseQuery}，自动携带 pageNum/pageSize。
 * 配合 {@link com.sloth.boot.starter.mybatis.core.LambdaQueryWrapperX} 的 likeIfPresent/eqIfPresent 实现动态过滤。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户分页查询条件")
public class UserQuery extends BaseQuery {

    /**
     * 用户名（模糊匹配）
     */
    @Schema(description = "用户名（模糊匹配）", example = "dev")
    private String username;

    /**
     * 手机号（精确匹配）
     */
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    /**
     * 所属部门ID
     */
    @Schema(description = "所属部门ID", example = "2")
    private Long deptId;

    /**
     * 状态
     */
    @Schema(description = "状态（0-正常, 1-停用）", example = "0")
    private Integer status;
}
```

- [ ] **Step 5: 创建 SysUserVO**

```java
package com.sloth.boot.example.model.user.vo;

import com.sloth.boot.common.security.desensitize.Desensitize;
import com.sloth.boot.common.security.desensitize.DesensitizeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 用户视图对象（含数据脱敏）
 * <p>
 * 演示 {@link Desensitize} 注解：返回给前端时自动对敏感字段进行脱敏处理。
 * <ul>
 *   <li>手机号：138****8000</li>
 *   <li>身份证号：110101********1234</li>
 *   <li>邮箱：z***n@sloth.boot</li>
 * </ul>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Schema(description = "用户视图对象（敏感字段自动脱敏）")
public class SysUserVO {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "1")
    private Long id;

    /**
     * 所属部门ID
     */
    @Schema(description = "所属部门ID", example = "2")
    private Long deptId;

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "dev_user")
    private String username;

    /**
     * 手机号（脱敏后展示）
     */
    @Desensitize(type = DesensitizeType.MOBILE)
    @Schema(description = "手机号（脱敏：138****8000）", example = "13800138000")
    private String phone;

    /**
     * 身份证号（脱敏后展示）
     */
    @Desensitize(type = DesensitizeType.ID_CARD)
    @Schema(description = "身份证号（脱敏：110101********1234）", example = "110101199001011234")
    private String idCard;

    /**
     * 邮箱（脱敏后展示）
     */
    @Desensitize(type = DesensitizeType.EMAIL)
    @Schema(description = "邮箱（脱敏：z***n@sloth.boot）", example = "dev@sloth.boot")
    private String email;

    /**
     * 性别
     */
    @Schema(description = "性别（0-未知, 1-男, 2-女）", example = "1")
    private Integer gender;

    /**
     * 状态
     */
    @Schema(description = "状态（0-正常, 1-停用）", example = "0")
    private Integer status;

    /**
     * 扩展信息
     */
    @Schema(description = "扩展信息（JSON自动反序列化）")
    private Map<String, Object> extraInfo;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createBy;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private String updateBy;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
```

---

## Task 7: DeptService + DeptController

**Files:**
- Create: `service/dept/DeptService.java`
- Create: `controller/dept/DeptController.java`

- [ ] **Step 1: 创建 DeptService**

```java
package com.sloth.boot.example.service.dept;

import com.sloth.boot.common.util.TreeUtil;
import com.sloth.boot.example.domain.entity.SysDept;
import com.sloth.boot.example.domain.mapper.SysDeptMapper;
import com.sloth.boot.example.model.dept.request.DeptCreateRequest;
import com.sloth.boot.example.model.dept.vo.DeptVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 部门服务
 * <p>
 * 演示 MyBatis-Plus 核心能力：
 * <ul>
 *   <li>BaseMapperX — 基础 CRUD、分页查询</li>
 *   <li>insertBatch — 单语句批量插入</li>
 *   <li>自动填充 — createBy/updateBy/createTime/updateTime 自动写入</li>
 *   <li>逻辑删除 — @TableLogic 软删除</li>
 *   <li>乐观锁 — @Version 版本号控制</li>
 *   <li>TreeUtil — 构建部门树</li>
 * </ul>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeptService {

    private final SysDeptMapper deptMapper;

    /**
     * 创建部门
     *
     * @param request 创建请求
     * @return 创建的部门实体
     */
    public SysDept create(DeptCreateRequest request) {
        SysDept dept = new SysDept();
        dept.setName(request.getName());
        dept.setParentId(request.getParentId() != null ? request.getParentId() : 0L);
        dept.setLeader(request.getLeader());
        dept.setSort(request.getSort() != null ? request.getSort() : 0);
        dept.setStatus(request.getStatus() != null ? request.getStatus() : 0);
        // ancestors 由父级推导：顶级为 "0"，子级追加父ID
        if (dept.getParentId() == 0) {
            dept.setAncestors("0");
        } else {
            SysDept parent = deptMapper.selectById(dept.getParentId());
            dept.setAncestors(parent != null ? parent.getAncestors() + "," + parent.getId() : "0");
        }
        deptMapper.insert(dept);
        log.info("创建部门成功: id={}, name={}", dept.getId(), dept.getName());
        return dept;
    }

    /**
     * 构建部门树
     * <p>
     * 查询全部部门后，使用 {@link TreeUtil#buildTree} 将平铺列表转为树形结构。
     *
     * @return 部门树（根节点列表）
     */
    public List<DeptVO> getTree() {
        List<SysDept> depts = deptMapper.selectList(null);
        List<DeptVO> voList = depts.stream().map(this::toVO).toList();
        return TreeUtil.buildTree(voList, 0L);
    }

    /**
     * 根据ID查询部门
     *
     * @param id 部门ID
     * @return 部门实体
     */
    public SysDept getById(Long id) {
        return deptMapper.selectById(id);
    }

    /**
     * 更新部门（乐观锁）
     * <p>
     * 更新时 MyBatis-Plus 自动在 WHERE 中添加 version 条件，
     * 如果 version 不匹配则更新失败（OptimisticLockerInnerInterceptor）。
     *
     * @param dept 部门实体（必须包含 id 和 version）
     * @return 是否更新成功
     */
    public boolean update(SysDept dept) {
        int rows = deptMapper.updateById(dept);
        log.info("更新部门: id={}, affected={}", dept.getId(), rows);
        return rows > 0;
    }

    /**
     * 删除部门（逻辑删除）
     * <p>
     * 不会物理删除数据，而是将 deleted 字段设为 1（@TableLogic）。
     *
     * @param id 部门ID
     * @return 是否删除成功
     */
    public boolean deleteById(Long id) {
        int rows = deptMapper.deleteById(id);
        log.info("逻辑删除部门: id={}, affected={}", id, rows);
        return rows > 0;
    }

    /**
     * 批量导入部门
     * <p>
     * 使用 {@code insertBatch}（InsertBatchSqlInjector 注入），单条 SQL 批量插入，性能远优于循环逐条 insert。
     *
     * @param depts 部门列表
     * @return 插入行数
     */
    public int batchImport(List<SysDept> depts) {
        if (depts == null || depts.isEmpty()) {
            return 0;
        }
        int rows = deptMapper.insertBatch(depts);
        log.info("批量导入部门: count={}", rows);
        return rows;
    }

    /**
     * 带数据权限查询部门列表
     * <p>
     * Mapper 方法标注了 @DataScope，根据 UserContext.getDataScope() 自动追加 WHERE 条件。
     * 需要先通过 /api/system/login 设置 UserContext。
     *
     * @return 部门列表
     */
    public List<SysDept> listWithScope() {
        return deptMapper.selectListWithScope(null);
    }

    /**
     * SysDept → DeptVO 转换
     */
    private DeptVO toVO(SysDept dept) {
        DeptVO vo = new DeptVO();
        vo.setId(dept.getId());
        vo.setName(dept.getName());
        vo.setParentId(dept.getParentId());
        vo.setLeader(dept.getLeader());
        vo.setStatus(dept.getStatus());
        vo.setAncestors(dept.getAncestors());
        vo.setCreateBy(dept.getCreateBy());
        vo.setCreateTime(dept.getCreateTime());
        return vo;
    }
}
```

- [ ] **Step 2: 创建 DeptController**

```java
package com.sloth.boot.example.controller.dept;

import com.sloth.boot.common.log.annotation.OperateLog;
import com.sloth.boot.common.log.annotation.OperateTypeEnum;
import com.sloth.boot.common.result.R;
import com.sloth.boot.example.domain.entity.SysDept;
import com.sloth.boot.example.model.dept.request.DeptCreateRequest;
import com.sloth.boot.example.model.dept.vo.DeptVO;
import com.sloth.boot.example.service.dept.DeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理接口
 * <p>
 * 演示 MyBatis-Plus ORM 全链路能力：
 * BaseMapperX CRUD、TreeUtil 树构建、insertBatch 批量导入、
 * 乐观锁更新、逻辑删除、@DataScope 数据权限、自动填充
 */
@Tag(name = "部门管理", description = "演示 MyBatis-Plus ORM 能力：CRUD、树结构、批量插入、乐观锁、逻辑删除、数据权限")
@RestController
@RequestMapping("/api/dept")
@RequiredArgsConstructor
public class DeptController {

    private final DeptService deptService;

    /**
     * 创建部门
     * <p>
     * 演示：自动填充 createBy/createTime，雪花ID 生成
     */
    @Operation(summary = "创建部门", description = "创建部门，自动填充创建人和创建时间，使用雪花算法生成ID")
    @OperateLog(module = "部门管理", description = "创建部门", type = OperateTypeEnum.CREATE)
    @PostMapping
    public R<SysDept> create(@Valid @RequestBody DeptCreateRequest request) {
        return R.ok(deptService.create(request));
    }

    /**
     * 获取部门树
     * <p>
     * 演示：TreeUtil.buildTree 将平铺列表转为树形结构
     */
    @Operation(summary = "获取部门树", description = "查询全部部门并构建树形结构返回")
    @GetMapping("/tree")
    public R<List<DeptVO>> getTree() {
        return R.ok(deptService.getTree());
    }

    /**
     * 根据ID查询部门
     */
    @Operation(summary = "查询部门详情", description = "根据部门ID查询部门信息")
    @Parameter(name = "id", description = "部门ID", required = true, example = "1")
    @GetMapping("/{id}")
    public R<SysDept> getById(@PathVariable Long id) {
        return R.ok(deptService.getById(id));
    }

    /**
     * 更新部门（乐观锁）
     * <p>
     * 演示：@Version 乐观锁，更新时必须传入正确的 version 值
     */
    @Operation(summary = "更新部门", description = "更新部门信息，使用乐观锁防止并发更新冲突，需传入正确的 version 值")
    @OperateLog(module = "部门管理", description = "更新部门", type = OperateTypeEnum.UPDATE)
    @PutMapping
    public R<Boolean> update(@RequestBody SysDept dept) {
        return R.ok(deptService.update(dept));
    }

    /**
     * 删除部门（逻辑删除）
     * <p>
     * 演示：@TableLogic 逻辑删除，数据不会物理删除
     */
    @Operation(summary = "删除部门", description = "逻辑删除部门（数据不会物理删除，标记 deleted=1）")
    @Parameter(name = "id", description = "部门ID", required = true, example = "1")
    @OperateLog(module = "部门管理", description = "删除部门", type = OperateTypeEnum.DELETE)
    @DeleteMapping("/{id}")
    public R<Boolean> deleteById(@PathVariable Long id) {
        return R.ok(deptService.deleteById(id));
    }

    /**
     * 批量导入部门
     * <p>
     * 演示：insertBatch 单语句批量插入，性能远优于逐条 insert
     */
    @Operation(summary = "批量导入部门", description = "使用 insertBatch 单条SQL批量插入，性能优于循环逐条insert")
    @PostMapping("/import")
    public R<Integer> batchImport(@RequestBody List<SysDept> depts) {
        return R.ok(deptService.batchImport(depts));
    }

    /**
     * 数据权限演示
     * <p>
     * 演示：@DataScope 数据权限过滤。
     * 需先通过 /api/system/login 登录设置 UserContext，不同 dataScope 值返回不同数据范围。
     */
    @Operation(summary = "数据权限查询", description = "演示 @DataScope 数据权限：需先登录，不同 dataScope 返回不同范围数据")
    @GetMapping("/scope")
    public R<List<SysDept>> listWithScope() {
        return R.ok(deptService.listWithScope());
    }
}
```

---

## Task 8: UserService + UserController

**Files:**
- Create: `service/user/UserService.java`
- Create: `controller/user/UserController.java`

- [ ] **Step 1: 创建 UserService**

```java
package com.sloth.boot.example.service.user;

import com.sloth.boot.example.domain.entity.SysUser;
import com.sloth.boot.example.domain.mapper.SysUserMapper;
import com.sloth.boot.example.model.user.request.UserCreateRequest;
import com.sloth.boot.example.model.user.request.UserQuery;
import com.sloth.boot.example.model.user.vo.SysUserVO;
import com.sloth.boot.common.result.PageResult;
import com.sloth.boot.starter.mybatis.core.LambdaQueryWrapperX;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务
 * <p>
 * 演示 MyBatis-Plus 高级能力：
 * <ul>
 *   <li>BaseMapperX.selectPage — 分页查询</li>
 *   <li>LambdaQueryWrapperX — null-safe 条件拼接（likeIfPresent/eqIfPresent）</li>
 *   <li>EncryptTypeHandler — phone/idCard 字段 AES 自动加解密</li>
 *   <li>JsonTypeHandler — extraInfo 字段 JSON 自动序列化/反序列化</li>
 *   <li>insertBatch — 单语句批量插入</li>
 *   <li>@DataPermission — 增强型数据权限</li>
 *   <li>@Desensitize — VO 层数据脱敏</li>
 * </ul>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final SysUserMapper userMapper;

    /**
     * 创建用户
     * <p>
     * phone 和 idCard 通过 EncryptTypeHandler 自动加密后存储到数据库。
     *
     * @param request 创建请求
     * @return 创建的用户实体（字段已解密）
     */
    public SysUser create(UserCreateRequest request) {
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPhone(request.getPhone());
        user.setIdCard(request.getIdCard());
        user.setEmail(request.getEmail());
        user.setGender(request.getGender() != null ? request.getGender() : 0);
        user.setStatus(request.getStatus() != null ? request.getStatus() : 0);
        user.setDeptId(request.getDeptId());
        user.setExtraInfo(request.getExtraInfo());
        userMapper.insert(user);
        log.info("创建用户成功: id={}, username={}", user.getId(), user.getUsername());
        return user;
    }

    /**
     * 分页查询用户（动态条件过滤）
     * <p>
     * 使用 {@link LambdaQueryWrapperX} 的 null-safe 条件方法：
     * <ul>
     *   <li>{@code likeIfPresent} — username 非空时模糊匹配</li>
     *   <li>{@code eqIfPresent} — phone/deptId/status 非空时精确匹配</li>
     * </ul>
     * 所有条件均为可选，传 null 自动跳过，无需手写 if 判断。
     *
     * @param query 查询条件（继承 BaseQuery，自动携带 pageNum/pageSize）
     * @return 分页结果
     */
    public PageResult<SysUser> page(UserQuery query) {
        LambdaQueryWrapperX<SysUser> wrapper = new LambdaQueryWrapperX<SysUser>()
                .likeIfPresent(SysUser::getUsername, query.getUsername())
                .eqIfPresent(SysUser::getPhone, query.getPhone())
                .eqIfPresent(SysUser::getDeptId, query.getDeptId())
                .eqIfPresent(SysUser::getStatus, query.getStatus())
                .orderByDesc(SysUser::getCreateTime);
        return userMapper.selectPage(query, wrapper);
    }

    /**
     * 根据ID查询用户
     * <p>
     * phone/idCard 通过 EncryptTypeHandler 自动从数据库解密后返回明文。
     *
     * @param id 用户ID
     * @return 用户实体（敏感字段已解密）
     */
    public SysUser getById(Long id) {
        return userMapper.selectById(id);
    }

    /**
     * 查询用户脱敏视图
     * <p>
     * 返回 {@link SysUserVO}，phone/idCard/email 通过 @Desensitize 注解自动脱敏：
     * <ul>
     *   <li>手机号：138****8000</li>
     *   <li>身份证号：110101********1234</li>
     *   <li>邮箱：d***r@sloth.boot</li>
     * </ul>
     *
     * @param id 用户ID
     * @return 脱敏后的用户 VO
     */
    public SysUserVO getVO(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            return null;
        }
        return toVO(user);
    }

    /**
     * 更新用户（乐观锁）
     *
     * @param user 用户实体（必须包含 id 和 version）
     * @return 是否更新成功
     */
    public boolean update(SysUser user) {
        int rows = userMapper.updateById(user);
        log.info("更新用户: id={}, affected={}", user.getId(), rows);
        return rows > 0;
    }

    /**
     * 删除用户（逻辑删除）
     *
     * @param id 用户ID
     * @return 是否删除成功
     */
    public boolean deleteById(Long id) {
        int rows = userMapper.deleteById(id);
        log.info("逻辑删除用户: id={}, affected={}", id, rows);
        return rows > 0;
    }

    /**
     * 批量导入用户
     * <p>
     * 使用 insertBatch 单语句批量插入，phone/idCard 自动加密。
     *
     * @param users 用户列表
     * @return 插入行数
     */
    public int batchImport(List<SysUser> users) {
        if (users == null || users.isEmpty()) {
            return 0;
        }
        int rows = userMapper.insertBatch(users);
        log.info("批量导入用户: count={}", rows);
        return rows;
    }

    /**
     * 带数据权限分页查询
     * <p>
     * Mapper 方法标注了 @DataPermission，根据 UserContext.getDataScope() 自动追加 WHERE 条件。
     * 需要先通过 /api/system/login 设置 UserContext。
     *
     * @param query 查询条件
     * @return 分页结果
     */
    public PageResult<SysUser> pageWithPermission(UserQuery query) {
        LambdaQueryWrapperX<SysUser> wrapper = new LambdaQueryWrapperX<SysUser>()
                .likeIfPresent(SysUser::getUsername, query.getUsername())
                .eqIfPresent(SysUser::getDeptId, query.getDeptId())
                .eqIfPresent(SysUser::getStatus, query.getStatus());
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysUser> page =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(query.getPageNum(), query.getPageSize());
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysUser> result =
                userMapper.selectPageWithPermission(page, wrapper);
        return com.sloth.boot.starter.mybatis.core.BaseMapperX.toPageResult(result);
    }

    /**
     * SysUser → SysUserVO 转换
     */
    private SysUserVO toVO(SysUser user) {
        SysUserVO vo = new SysUserVO();
        vo.setId(user.getId());
        vo.setDeptId(user.getDeptId());
        vo.setUsername(user.getUsername());
        vo.setPhone(user.getPhone());
        vo.setIdCard(user.getIdCard());
        vo.setEmail(user.getEmail());
        vo.setGender(user.getGender());
        vo.setStatus(user.getStatus());
        vo.setExtraInfo(user.getExtraInfo());
        vo.setCreateBy(user.getCreateBy());
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateBy(user.getUpdateBy());
        vo.setUpdateTime(user.getUpdateTime());
        return vo;
    }
}
```

- [ ] **Step 2: 创建 UserController**

```java
package com.sloth.boot.example.controller.user;

import com.sloth.boot.common.log.annotation.OperateLog;
import com.sloth.boot.common.log.annotation.OperateTypeEnum;
import com.sloth.boot.common.result.PageResult;
import com.sloth.boot.common.result.R;
import com.sloth.boot.example.domain.entity.SysUser;
import com.sloth.boot.example.model.user.request.UserCreateRequest;
import com.sloth.boot.example.model.user.request.UserQuery;
import com.sloth.boot.example.model.user.vo.SysUserVO;
import com.sloth.boot.example.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理接口
 * <p>
 * 演示 MyBatis-Plus 高级 ORM 能力：
 * EncryptTypeHandler 加解密、JsonTypeHandler JSON 存储、
 * LambdaQueryWrapperX 动态条件、@DataPermission 数据权限、
 * @Desensitize 数据脱敏、@Phone/@IdCard 自定义校验、insertBatch 批量导入
 */
@Tag(name = "用户管理", description = "演示字段加密、JSON存储、动态查询、数据权限、数据脱敏、自定义校验、批量导入等能力")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 创建用户
     * <p>
     * 演示：
     * <ul>
     *   <li>@Phone / @IdCard — 自定义参数校验</li>
     *   <li>EncryptTypeHandler — phone/idCard 自动 AES 加密存储</li>
     *   <li>JsonTypeHandler — extraInfo 自动 JSON 序列化存储</li>
     * </ul>
     */
    @Operation(summary = "创建用户", description = "创建用户，手机号和身份证号自动AES加密存储，扩展信息自动JSON序列化存储")
    @OperateLog(module = "用户管理", description = "创建用户", type = OperateTypeEnum.CREATE)
    @PostMapping
    public R<SysUser> create(@Valid @RequestBody UserCreateRequest request) {
        return R.ok(userService.create(request));
    }

    /**
     * 分页查询用户
     * <p>
     * 演示：BaseMapperX.selectPage + LambdaQueryWrapperX 条件拼接，
     * 所有查询条件均可选，null 自动跳过。
     */
    @Operation(summary = "分页查询用户", description = "支持按用户名/手机号/部门/状态动态过滤，条件均可选")
    @GetMapping("/page")
    public R<PageResult<SysUser>> page(UserQuery query) {
        return R.ok(userService.page(query));
    }

    /**
     * 查询用户详情
     * <p>
     * 演示：EncryptTypeHandler 自动解密，返回明文手机号和身份证号
     */
    @Operation(summary = "查询用户详情", description = "查询用户，手机号和身份证号自动从数据库AES解密返回明文")
    @Parameter(name = "id", description = "用户ID", required = true, example = "1")
    @GetMapping("/{id}")
    public R<SysUser> getById(@PathVariable Long id) {
        return R.ok(userService.getById(id));
    }

    /**
     * 查询用户脱敏视图
     * <p>
     * 演示：@Desensitize 注解自动脱敏，
     * 手机号 → 138****8000，身份证号 → 110101********1234
     */
    @Operation(summary = "查询用户（脱敏）", description = "返回脱敏后的用户信息：手机号138****8000，身份证110101********1234")
    @Parameter(name = "id", description = "用户ID", required = true, example = "1")
    @GetMapping("/{id}/desensitize")
    public R<SysUserVO> getVO(@PathVariable Long id) {
        return R.ok(userService.getVO(id));
    }

    /**
     * 更新用户（乐观锁）
     * <p>
     * 演示：@Version 乐观锁，必须传入查询到的 version 值
     */
    @Operation(summary = "更新用户", description = "更新用户信息，使用乐观锁防止并发更新冲突")
    @OperateLog(module = "用户管理", description = "更新用户", type = OperateTypeEnum.UPDATE)
    @PutMapping
    public R<Boolean> update(@RequestBody SysUser user) {
        return R.ok(userService.update(user));
    }

    /**
     * 删除用户（逻辑删除）
     */
    @Operation(summary = "删除用户", description = "逻辑删除用户（标记 deleted=1）")
    @Parameter(name = "id", description = "用户ID", required = true, example = "1")
    @OperateLog(module = "用户管理", description = "删除用户", type = OperateTypeEnum.DELETE)
    @DeleteMapping("/{id}")
    public R<Boolean> deleteById(@PathVariable Long id) {
        return R.ok(userService.deleteById(id));
    }

    /**
     * 批量导入用户
     * <p>
     * 演示：insertBatch 单语句批量插入，phone/idCard 自动加密
     */
    @Operation(summary = "批量导入用户", description = "使用 insertBatch 单条SQL批量插入，敏感字段自动加密")
    @PostMapping("/import")
    public R<Integer> batchImport(@RequestBody List<SysUser> users) {
        return R.ok(userService.batchImport(users));
    }

    /**
     * 数据权限分页查询
     * <p>
     * 演示：@DataPermission 增强型数据权限。
     * 需先通过 /api/system/login 登录设置 UserContext。
     * 不同 dataScope 值返回不同范围数据。
     */
    @Operation(summary = "数据权限查询", description = "演示 @DataPermission 数据权限：需先登录，不同dataScope返回不同范围")
    @GetMapping("/scope")
    public R<PageResult<SysUser>> pageWithPermission(UserQuery query) {
        return R.ok(userService.pageWithPermission(query));
    }
}
```

---

## Task 9: 编译验证 + 清理

**Files:**
- Verify: all new and modified files

- [ ] **Step 1: 编译验证**

Run: `cd D:/Guo/IDEA/project/SlothBoot && mvn compile -pl sloth-boot-example/sloth-boot-example-service -am -q`
Expected: BUILD SUCCESS

- [ ] **Step 2: 检查未使用的 import**

确保迁移后的文件中没有残留的旧包路径 import。搜索旧包名：

Run: `grep -r "com.sloth.boot.example.dto" sloth-boot-example/`
Expected: 无匹配结果

Run: `grep -r "com.sloth.boot.example.controller\." sloth-boot-example/ --include="*.java" | grep -v "controller\." | head -20`
Expected: 无匹配（所有 controller import 已更新为子包路径）

- [ ] **Step 3: 确认删除旧文件**

确认 `controller/*.java`、`dto/*.java`、`service/*.java`（旧的扁平目录）已全部删除，新位置文件已就位。

Run: `ls sloth-boot-example/sloth-boot-example-service/src/main/java/com/sloth/boot/example/`
Expected: 只有 `Application.java` + `controller/`、`domain/`、`model/`、`service/` 四个目录
