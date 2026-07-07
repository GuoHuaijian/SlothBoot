package com.sloth.boot.example.observability.application.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品视图对象。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Schema(description = "商品信息")
public class ProductVO {

    @Schema(description = "商品ID", example = "3")
    private Long id;

    @Schema(description = "商品名称", example = "Sloth Mug")
    private String name;

    @Schema(description = "单价", example = "39.00")
    private BigDecimal price;

    @Schema(description = "库存", example = "500")
    private Integer stock;

    @Schema(description = "分类", example = "accessory")
    private String category;
}
