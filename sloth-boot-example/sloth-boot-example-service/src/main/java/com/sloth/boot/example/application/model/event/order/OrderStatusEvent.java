package com.sloth.boot.example.application.model.event.order;

import com.sloth.boot.common.event.BaseEvent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 订单状态变更事件。
 * <p>
 * 继承 BaseEvent，自动携带 traceId、eventTime、source 等上下文信息。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
@Schema(description = "订单状态变更事件")
public class OrderStatusEvent extends BaseEvent {

    @Schema(description = "订单ID", example = "1")
    private final Long orderId;

    @Schema(description = "新状态：1-待支付 2-已支付 3-已完成", example = "2")
    private final String status;

    @Schema(description = "状态描述", example = "订单已支付成功")
    private final String message;

    /**
     * 构造函数
     *
     * @param source  事件来源
     * @param orderId 订单ID
     * @param status  新状态
     * @param message 状态描述
     */
    public OrderStatusEvent(Object source, Long orderId, String status, String message) {
        super(source);
        this.orderId = orderId;
        this.status = status;
        this.message = message;
    }

    /**
     * 创建订单状态变更事件的静态工厂方法
     *
     * @param source  事件来源
     * @param orderId 订单ID
     * @param status  新状态
     * @param message 状态描述
     * @return OrderStatusEvent
     */
    public static OrderStatusEvent of(Object source, Long orderId, String status, String message) {
        return new OrderStatusEvent(source, orderId, status, message);
    }
}
