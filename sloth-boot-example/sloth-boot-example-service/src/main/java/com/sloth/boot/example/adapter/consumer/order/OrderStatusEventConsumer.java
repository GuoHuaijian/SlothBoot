package com.sloth.boot.example.adapter.consumer.order;

import com.sloth.boot.example.application.model.event.order.OrderStatusEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 订单状态事件消费者。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Component
public class OrderStatusEventConsumer {

    @Async
    @EventListener
    public void onOrderStatusChanged(OrderStatusEvent event) {
        log.info("收到订单状态变更事件: orderId={}, status={}, message={}",
            event.orderId(), event.status(), event.message());
    }
}
