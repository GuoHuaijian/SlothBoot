# Sloth Boot Starter ES

Elasticsearch 操作增强组件，基于 Spring Data Elasticsearch 封装 `EsTemplate`，提供索引管理、CRUD、分页查询及高亮搜索能力。

## Maven 依赖

```xml
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-starter-es</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## 配置项

| 配置项 | 类型 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `sloth.es.enabled` | `boolean` | `true` | 是否启用 ES Starter |
| `sloth.es.default-index` | `String` | - | 默认索引名 |
| `sloth.es.timeout` | `long` | `5` | 查询超时时间（秒） |

## 核心组件

| 组件 | 说明 |
| --- | --- |
| `EsTemplate` | ES 操作模板，提供索引管理、CRUD、分页、高亮方法 |
| `EsProperties` | 配置属性 |
| `EsAutoConfiguration` | 自动注册 `EsTemplate` Bean |

## 配置示例

```yaml
spring:
  elasticsearch:
    uris: http://localhost:9200

sloth:
  es:
    enabled: true
    default-index: product_index
    timeout: 5
```

## 索引管理

```java
@Service
@RequiredArgsConstructor
public class ProductIndexService {

    private final EsTemplate esTemplate;

    public void createIndex() {
        esTemplate.createIndex("product_index");
    }

    public boolean exists() {
        return esTemplate.existIndex("product_index");
    }

    public void dropIndex() {
        esTemplate.deleteIndex("product_index");
    }
}
```

## 文档 CRUD

```java
@Document(indexName = "product_index")
@Data
public class Product {
    @Id
    private String id;
    private String name;
    private String category;
    private BigDecimal price;
}

@Service
@RequiredArgsConstructor
public class ProductService {

    private final EsTemplate esTemplate;

    public Product save(Product product) {
        return esTemplate.save(product);
    }

    public void batchSave(List<Product> products) {
        esTemplate.batchSave(products);
    }

    public void delete(Product product) {
        esTemplate.delete(product);
    }
}
```

## 分页与高亮搜索

```java
// 分页查询
public Page<Product> search(String keyword, Pageable pageable) {
    Query query = MatchQuery.of(m -> m.field("name").query(keyword))._toQuery();
    return esTemplate.page(query, pageable, Product.class);
}

// 高亮搜索
public List<Map<String, Object>> highlightSearch(String keyword) {
    Query query = MatchQuery.of(m -> m.field("name").query(keyword))._toQuery();
    return esTemplate.highlight(query, Product.class);
}
```

## FAQ

**Q: `EsTemplate` 与 `ElasticsearchRestTemplate` 有什么区别？**
A: `EsTemplate` 是轻量封装，统一了常用操作入口并增加了超时配置，底层仍委托 `ElasticsearchOperations`。

**Q: 如何自定义映射？**
A: 使用 Spring Data 的 `@Document`、`@Field` 注解标注实体类，或通过 `IndexOperations.putMapping()` 手动创建。
