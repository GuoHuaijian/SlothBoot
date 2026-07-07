package com.sloth.boot.example.observability.infrastructure.model.po.product;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sloth.boot.starter.mybatis.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 商品实体。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("demo_product")
public class DemoProduct extends BaseEntity {

    private String name;

    private BigDecimal price;

    private Integer stock;

    private String category;
}
