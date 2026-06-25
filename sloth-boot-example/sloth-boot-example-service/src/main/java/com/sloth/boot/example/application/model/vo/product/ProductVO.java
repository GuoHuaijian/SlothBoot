package com.sloth.boot.example.application.model.vo.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品视图对象。
 * <p>
 * 用于接口响应的商品信息。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Schema(description = "商品视图对象")
public class ProductVO {

    /** 商品ID */
    @Schema(description = "商品ID", example = "1")
    private Long id;

    /** 商品名称 */
    @Schema(description = "商品名称", example = "Sloth Boot 企业版授权")
    private String name;

    /** 商品价格 */
    @Schema(description = "商品价格", example = "9999.00")
    private BigDecimal price;

    /** 库存数量 */
    @Schema(description = "库存数量", example = "100")
    private Integer stock;

    /** 商品描述 */
    @Schema(description = "商品描述")
    private String description;

    /** 状态（0-上架, 1-下架） */
    @Schema(description = "状态", example = "0")
    private Integer status;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
