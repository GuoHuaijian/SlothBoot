package com.sloth.boot.example.observability.infrastructure.model.po.order;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sloth.boot.example.observability.application.model.enums.order.OrderStatus;
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

    private String orderNo;

    private Long userId;

    private Long productId;

    private String productName;

    private Integer quantity;

    private BigDecimal amount;

    private OrderStatus status;
}
