package com.sloth.boot.example.infrastructure.model.po.product;

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
@TableName("product")
public class Product extends BaseEntity {

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 状态
     */
    private Integer status;
}
