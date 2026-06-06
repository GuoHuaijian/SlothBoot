package com.sloth.boot.example.model.order.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 订单创建请求参数
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Schema(description = "订单创建请求")
public class OrderCreateRequest {

    @Schema(description = "商品ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productId;

    @Schema(description = "购买数量", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;
}
