package com.sloth.boot.example.application.model.vo.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单视图对象。
 * <p>
 * 用于接口响应的订单信息。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Schema(description = "订单信息")
public class OrderVO {

    /** 订单ID */
    @Schema(description = "订单ID", example = "1")
    private Long id;

    /** 用户ID */
    @Schema(description = "用户ID", example = "1")
    private Long userId;

    /** 商品ID */
    @Schema(description = "商品ID", example = "1")
    private Long productId;

    /** 商品名称 */
    @Schema(description = "商品名称", example = "iPhone 16 Pro Max")
    private String productName;

    /** 订单金额 */
    @Schema(description = "订单金额", example = "19998.00")
    private BigDecimal amount;

    /** 购买数量 */
    @Schema(description = "购买数量", example = "2")
    private Integer quantity;

    /** 订单状态：CREATED-待支付 PAID-已支付 */
    @Schema(description = "订单状态：CREATED-待支付 PAID-已支付", example = "CREATED")
    private String status;

    /** 创建时间 */
    @Schema(description = "创建时间", example = "2026-06-12 10:00:00")
    private LocalDateTime createTime;

    /** 更新时间 */
    @Schema(description = "更新时间", example = "2026-06-12 10:00:00")
    private LocalDateTime updateTime;
}
