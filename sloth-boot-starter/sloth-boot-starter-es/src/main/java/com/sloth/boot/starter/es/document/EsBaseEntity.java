package com.sloth.boot.starter.es.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * ES 文档基类。
 * <p>
 * 所有 ES 实体可继承此类，统一管理文档 ID。
 * 示例：
 * <pre>{@code
 * @Document(indexName = "product")
 * public class Product extends EsBaseEntity {
 *     private String name;
 *     private BigDecimal price;
 * }
 * }</pre>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
@Setter
public abstract class EsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文档唯一标识。
     */
    @Id
    private String id;
}
