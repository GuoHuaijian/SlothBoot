# sloth-boot-common-test

> SlothBoot 测试基类模块，提供 SpringBoot 测试、MockMvc 测试、MyBatis Mapper 测试的基类和 Mock 工具。

## Maven 依赖

```xml
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-common-test</artifactId>
    <scope>test</scope>
</dependency>
```

## 核心组件

| 组件 | 说明 |
|------|------|
| `BaseSpringBootTest` | SpringBoot 集成测试基类 |
| `BaseMockMvcTest` | MockMvc 控制器测试基类 |
| `BaseMapperTest` | MyBatis Mapper 测试基类（H2 内存数据库） |
| `@MockUser` | 测试用户注解，自动注入 UserContext |
| `MockUserTestExecutionListener` | 测试执行监听器，配合 `@MockUser` 使用 |

## 使用示例

### 集成测试

```java
@SpringBootTest
class UserServiceTest extends BaseSpringBootTest {

    @Autowired
    private UserService userService;

    @Test
    void testCreateUser() {
        // 测试逻辑
    }
}
```

### 控制器测试

```java
@WebMvcTest(UserController.class)
class UserControllerTest extends BaseMockMvcTest {

    @MockBean
    private UserService userService;

    @Test
    void testGetUser() throws Exception {
        mockMvc.perform(get("/api/user/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0));
    }
}
```

### 带 Mock 用户的测试

```java
@SpringBootTest
class OrderServiceTest extends BaseSpringBootTest {

    @Test
    @MockUser(userId = 1L, username = "admin", roles = {"admin"})
    void testCreateOrder() {
        // UserContext 已自动注入，可直接使用
        Long userId = UserContext.getUserId(); // 1L
    }
}
```

### Mapper 测试

```java
class UserMapperTest extends BaseMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void testInsert() {
        UserEntity user = new UserEntity();
        user.setUsername("test");
        userMapper.insert(user);
        assertNotNull(user.getId());
    }
}
```
