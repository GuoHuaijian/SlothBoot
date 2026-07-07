package com.sloth.boot.example.observability.application.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单视图对象。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Schema(description = "订单信息")
public class OrderVO {

    @Schema(description = "订单ID", example = "1001")
    private Long id;

    @Schema(description = "订单号", example = "ORD20260601001")
    private String orderNo;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "商品ID", example = "1")
    private Long productId;

    @Schema(description = "商品名称", example = "Sloth Hoodie")
    private String productName;

    @Schema(description = "购买数量", example = "2")
    private Integer quantity;

    @Schema(description = "订单金额", example = "398.00")
    private BigDecimal amount;

    @Schema(description = "订单状态：PENDING/CREATED/PAID/SHIPPED/COMPLETED/CANCELLED", example = "PAID")
    private String status;

    @Schema(description = "创建时间", example = "2026-06-01 09:15:23")
    private LocalDateTime createTime;
}
