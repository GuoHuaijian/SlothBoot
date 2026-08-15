package com.sloth.boot.example.application.model.event.order;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 订单状态变更事件。
 * <p>
 * 普通 POJO 事件，可序列化后通过 Redis Pub/Sub 或 Spring 事件机制传播。
 *
 * @param orderId 订单ID
 * @param status  新状态：1-待支付 2-已支付 3-已完成
 * @param message 状态描述
 * @author sloth-boot
 * @since 1.0.0
 */
@Schema(description = "订单状态变更事件")
public record OrderStatusEvent(@Schema(description = "订单ID", example = "1") Long orderId,
                               @Schema(description = "新状态：1-待支付 2-已支付 3-已完成", example = "2") String status,
                               @Schema(description = "状态描述", example = "订单已支付成功") String message) {

    /**
     * 创建订单状态变更事件。
     *
     * @param orderId 订单ID
     * @param status  新状态
     * @param message 状态描述
     * @return OrderStatusEvent
     */
    public static OrderStatusEvent of(Long orderId, String status, String message) {
        return new OrderStatusEvent(orderId, status, message);
    }
}
