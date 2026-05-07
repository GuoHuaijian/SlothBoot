# Sloth Boot Starter Seata

分布式事务组件，自动装配 Seata AT 模式，支持 Nacos 注册中心，开箱即用的全局事务管理。

## Maven 依赖

```xml
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-starter-seata</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## 配置项

| 配置项 | 类型 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `sloth.seata.enabled` | `boolean` | `false` | 是否启用 Seata（需显式开启） |
| `sloth.seata.tx-service-group` | `String` | `${spring.application.name}-tx-group` | 事务分组名称 |
| `sloth.seata.mode` | `String` | `AT` | 事务模式：`AT` / `XA` / `Saga` |

## 核心组件

| 组件 | 说明 |
| --- | --- |
| `SeataAutoConfiguration` | 自动注册 `GlobalTransactionScanner` |
| `SeataProperties` | 配置属性 |

## 配置示例

```yaml
sloth:
  seata:
    enabled: true
    tx-service-group: ${spring.application.name}-tx-group
    mode: AT

# Seata Server 配置
seata:
  registry:
    type: nacos
    nacos:
      server-addr: localhost:8848
      group: SEATA_GROUP
  config:
    type: nacos
    nacos:
      server-addr: localhost:8848
      group: SEATA_GROUP
```

## 使用示例

```java
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final StorageClient storageClient;
    private final AccountClient accountClient;

    @GlobalTransactional(name = "create-order", rollbackFor = Exception.class)
    public void createOrder(OrderDTO dto) {
        // 1. 创建订单
        orderMapper.insert(buildOrder(dto));

        // 2. 扣减库存（远程调用）
        storageClient.deduct(dto.getProductId(), dto.getCount());

        // 3. 扣减余额（远程调用）
        accountClient.debit(dto.getUserId(), dto.getTotalAmount());
    }
}
```

## 工作原理

1. `sloth.seata.enabled=true` 时自动创建 `GlobalTransactionScanner`
2. 事务分组名默认为 `{应用名}-tx-group`，可通过 `tx-service-group` 覆盖
3. AT 模式下，业务 SQL 自动被 Seata 代理，生成 undo log
4. 全局事务提交时删除 undo log，回滚时用 undo log 反向补偿

## FAQ

**Q: 为什么默认 `enabled=false`？**
A: Seata 依赖独立的 Seata Server 部署，默认关闭避免未配置时启动报错。使用时需显式设置为 `true`。

**Q: `tx-service-group` 如何与 Seata Server 对应？**
A: 在 Nacos 的 Seata 配置中，`service.vgroupMapping.{tx-service-group}` 需映射到实际的 Seata Server 集群名称。

**Q: AT 模式有什么前提条件？**
A: 业务表需要有主键，数据源需被 Seata `DataSourceProxy` 代理，且需在数据库中创建 `undo_log` 表。
