package com.sloth.boot.example.infrastructure.model.po.order;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sloth.boot.example.application.model.enums.order.OrderStatus;
import com.sloth.boot.starter.mybatis.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 订单实体。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("demo_order")
public class DemoOrder extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 购买数量
     */
    private Integer quantity;

    /**
     * 订单金额
     */
    private BigDecimal totalPrice;

    /**
     * 状态
     */
    private OrderStatus status;
}
