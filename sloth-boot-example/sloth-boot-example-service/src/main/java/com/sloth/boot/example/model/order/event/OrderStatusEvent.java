package com.sloth.boot.example.model.order.event;

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
public class OrderStatusEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long orderId;

    private String status;

    private String message;

    private LocalDateTime eventTime;
}
