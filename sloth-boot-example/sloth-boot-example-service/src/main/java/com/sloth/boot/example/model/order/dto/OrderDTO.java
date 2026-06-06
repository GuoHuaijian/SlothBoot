package com.sloth.boot.example.model.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单数据传输对象
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Schema(description = "订单数据传输对象")
public class OrderDTO {

    @Schema(description = "订单ID", example = "1")
    private Long id;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "商品ID", example = "1")
    private Long productId;

    @Schema(description = "商品名称", example = "iPhone 16")
    private String productName;

    @Schema(description = "订单金额", example = "9999.00")
    private BigDecimal amount;

    @Schema(description = "购买数量", example = "1")
    private Integer quantity;

    @Schema(description = "订单状态", example = "CREATED")
    private String status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
