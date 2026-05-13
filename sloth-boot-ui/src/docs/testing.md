# 测试指南

## 测试基类

SlothBoot 提供了 `sloth-boot-common-test` 模块，包含常用的测试基类。

### BaseSpringBootTest

Spring Boot 集成测试基类，自动加载完整 Spring 上下文。

```java
@SpringBootTest
class MyServiceTest extends BaseSpringBootTest {

    @Autowired
    private MyService myService;

    @Test
    void testSomething() {
        // ...
    }
}
```

### BaseMockMvcTest

MockMvc 控制器测试基类，适用于 Web 层测试。

```java
@WebMvcTest(MyController.class)
class MyControllerTest extends BaseMockMvcTest {

    @MockBean
    private MyService myService;

    @Test
    void testEndpoint() throws Exception {
        mockMvc.perform(get("/api/test"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0));
    }
}
```

### BaseMapperTest

MyBatis Mapper 测试基类，使用 H2 内存数据库。

```java
class MyMapperTest extends BaseMapperTest {

    @Autowired
    private MyMapper myMapper;

    @Test
    void testInsert() {
        // 使用 H2 内存数据库测试
    }
}
```

## Mock 用户

使用 `@MockUser` 注解在测试中模拟已登录用户：

```java
@Test
@MockUser(userId = 1L, username = "testuser", roles = {"admin"})
void testWithMockUser() {
    // UserContext 已自动注入
    assertEquals(1L, UserContext.getUserId());
    assertEquals("testuser", UserContext.getUsername());
}
```

## 运行测试

```bash
# 运行所有测试
mvn clean verify

# 运行特定模块测试
mvn test -pl sloth-boot-starter/sloth-boot-starter-web

# 跳过集成测试（带 @Tag("integration")）
mvn test -DexcludedGroups=com.sloth.boot.common.test.IntegrationTest
```

## 测试最佳实践

### 单元测试

- 使用 `@MockBean` 隔离外部依赖
- 测试类按功能分组，使用 `@Nested` 嵌套
- 使用 AssertJ 的流式断言提高可读性

### 集成测试

- 使用 `@SpringBootTest` 加载完整上下文
- 使用 Testcontainers 启动真实数据库和 Redis
- 测试类命名以 `IT` 后缀区分（如 `UserServiceIT`）

### Web 层测试

- 使用 `@WebMvcTest` 只加载 Web 层
- 使用 `MockMvc` 模拟 HTTP 请求
- 验证响应状态码、JSON 结构和业务逻辑

## 测试覆盖模块

SlothBoot 目前包含 53+ 单元测试，覆盖以下核心模块：

| 模块 | 测试内容 |
|------|---------|
| common-core | R 响应体、PageResult、BaseQuery、错误码 |
| starter-web | GlobalExceptionHandler 全场景覆盖 |
| starter-redis | 缓存工具、分布式锁、限流器 |
| starter-mybatis | MybatisPlusAutoConfiguration 条件装配 |
| starter-auth | AuthAutoConfiguration 条件装配 |
| starter-thread-pool | ThreadPoolAutoConfiguration 条件装配 |
| starter-monitor | MonitorAutoConfiguration 条件装配 |
