package com.sloth.boot.example.model.product.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品创建请求参数
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Schema(description = "商品创建请求")
public class ProductCreateRequest {

    @Schema(description = "商品名称", example = "iPhone 16", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "商品描述", example = "最新款苹果手机")
    private String description;

    @Schema(description = "商品价格", example = "9999.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal price;

    @Schema(description = "库存数量", example = "100")
    private Integer stock;

    @Schema(description = "商品分类", example = "电子产品")
    private String category;
}
