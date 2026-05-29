package com.sloth.boot.example.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sloth.boot.starter.mybatis.core.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 商品实体。
 * <p>
 * 演示基础 CRUD、乐观锁、逻辑删除、缓存策略（布隆过滤器 + 逻辑过期）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product")
@Schema(description = "商品实体")
public class Product extends BaseEntity {

    @Schema(description = "商品名称", example = "Sloth Boot 企业版授权")
    private String name;

    @Schema(description = "商品价格", example = "9999.00")
    private BigDecimal price;

    @Schema(description = "库存数量", example = "100")
    private Integer stock;

    @Schema(description = "商品描述")
    private String description;

    @Schema(description = "状态（0-上架, 1-下架）", example = "0")
    private Integer status;
}
