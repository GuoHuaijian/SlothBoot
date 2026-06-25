package com.sloth.boot.example.application.model.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单数据传输对象。
 * <p>
 * 用于层间传输订单数据，解耦持久化模型与业务模型。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Schema(description = "订单信息")
public class OrderDTO {

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
    @Schema(description = "商品名称", example = "Sloth Boot 企业版")
    private String productName;

    /** 订单金额 */
    @Schema(description = "订单金额", example = "19998.00")
    private BigDecimal amount;

    /** 购买数量 */
    @Schema(description = "购买数量", example = "2")
    private Integer quantity;

    /** 订单状态（CREATED-待支付, PAID-已支付） */
    @Schema(description = "状态", example = "CREATED")
    private String status;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /** 更新时间 */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
