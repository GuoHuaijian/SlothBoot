package com.sloth.boot.example.model.order.event;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 订单状态变更事件
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "订单状态变更事件")
public class OrderStatusEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "订单ID", example = "1")
    private Long orderId;

    @Schema(description = "订单状态", example = "PAID")
    private String status;

    @Schema(description = "状态变更消息", example = "支付成功")
    private String message;

    @Schema(description = "事件发生时间")
    private LocalDateTime eventTime;
}
