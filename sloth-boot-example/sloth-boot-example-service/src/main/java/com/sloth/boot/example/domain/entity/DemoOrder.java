package com.sloth.boot.example.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sloth.boot.starter.mybatis.core.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 订单实体。
 * <p>
 * 演示分布式锁、幂等注解、限流、Pub/Sub 事件。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("demo_order")
@Schema(description = "订单实体")
public class DemoOrder extends BaseEntity {

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "商品ID", example = "1")
    private Long productId;

    @Schema(description = "商品名称（冗余）", example = "Sloth Boot 企业版授权")
    private String productName;

    @Schema(description = "购买数量", example = "1")
    private Integer quantity;

    @Schema(description = "订单总价", example = "9999.00")
    private BigDecimal totalPrice;

    @Schema(description = "订单状态", example = "PENDING")
    private String status;
}
