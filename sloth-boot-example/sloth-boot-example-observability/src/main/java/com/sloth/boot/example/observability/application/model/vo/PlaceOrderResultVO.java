package com.sloth.boot.example.observability.application.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 级联下单结果。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Schema(description = "级联下单结果")
public class PlaceOrderResultVO {

    @Schema(description = "生成的订单ID", example = "1780000000000000001")
    private Long orderId;

    @Schema(description = "下单用户")
    private UserVO user;

    @Schema(description = "下单商品")
    private ProductVO product;

    @Schema(description = "下单数量", example = "2")
    private Integer quantity;

    @Schema(description = "订单金额", example = "78.00")
    private BigDecimal amount;

    @Schema(description = "订单状态", example = "CREATED")
    private String status;
}
