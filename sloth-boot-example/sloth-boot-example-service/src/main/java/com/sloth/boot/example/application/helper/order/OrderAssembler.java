package com.sloth.boot.example.application.helper.order;

import com.sloth.boot.example.application.model.convert.order.OrderConvert;
import com.sloth.boot.example.application.model.enums.order.OrderStatus;
import com.sloth.boot.example.application.model.form.order.OrderCreateForm;
import com.sloth.boot.example.infrastructure.model.po.order.DemoOrder;
import com.sloth.boot.example.infrastructure.model.po.product.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 订单对象组装器。
 * <p>
 * 负责将多个数据源（表单、商品、用户上下文）组装为完整的订单实体。
 * 与 {@link OrderConvert} 的区别：
 * - OrderConvert：单对象之间的简单映射（Form→Entity, Entity→VO）
 * - OrderAssembler：跨域数据的复杂组装（Form + Product + UserId → Entity）
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class OrderAssembler {

    private final OrderConvert orderConvert;

    /**
     * 将订单创建表单、商品信息和用户ID组装为完整的订单实体。
     * <p>
     * 组装逻辑：
     * 1. 通过 MapStruct 将表单转换为订单实体
     * 2. 设置用户ID
     * 3. 填充商品名称（冗余存储，避免查询时关联）
     * 4. 计算订单总价（商品单价 × 购买数量）
     * 5. 设置初始状态为已创建
     *
     * @param form    订单创建表单
     * @param product 商品信息
     * @param userId  用户ID
     * @return 组装完成的订单实体
     */
    public DemoOrder assembleOrder(OrderCreateForm form, Product product, Long userId) {
        DemoOrder order = orderConvert.toEntity(form);
        order.setUserId(userId);
        order.setProductName(product.getName());
        order.setTotalPrice(calculateTotalPrice(product.getPrice(), form.getQuantity()));
        order.setStatus(OrderStatus.CREATED);
        return order;
    }

    /**
     * 计算订单总价。
     *
     * @param unitPrice 商品单价
     * @param quantity  购买数量
     * @return 订单总价
     */
    private BigDecimal calculateTotalPrice(BigDecimal unitPrice, Integer quantity) {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
