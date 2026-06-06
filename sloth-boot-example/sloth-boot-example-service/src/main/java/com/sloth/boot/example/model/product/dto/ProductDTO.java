package com.sloth.boot.example.model.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品数据传输对象
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Schema(description = "商品数据传输对象")
public class ProductDTO {

    @Schema(description = "商品ID", example = "1")
    private Long id;

    @Schema(description = "商品名称", example = "iPhone 16")
    private String name;

    @Schema(description = "商品描述", example = "最新款苹果手机")
    private String description;

    @Schema(description = "商品价格", example = "9999.00")
    private BigDecimal price;

    @Schema(description = "库存数量", example = "100")
    private Integer stock;

    @Schema(description = "商品分类", example = "电子产品")
    private String category;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
