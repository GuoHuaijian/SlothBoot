package com.sloth.boot.example.observability.application.command.order;

import com.sloth.boot.common.exception.BizException;
import com.sloth.boot.example.observability.application.model.convert.OrderConvert;
import com.sloth.boot.example.observability.application.model.enums.order.OrderErrorCode;
import com.sloth.boot.example.observability.application.model.enums.order.OrderStatus;
import com.sloth.boot.example.observability.application.model.vo.OrderVO;
import com.sloth.boot.example.observability.infrastructure.model.po.order.DemoOrder;
import com.sloth.boot.example.observability.infrastructure.repository.mapper.DemoOrderMapper;
import com.sloth.boot.example.observability.application.helper.MetricsSupport;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.Meter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 支付订单命令（写操作）。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PayOrderCommand {

    private final DemoOrderMapper orderMapper;
    private final OrderConvert orderConvert;
    private final Meter meter;

    private final MetricsSupport.DoubleHistogramHolder orderPayLatency = new MetricsSupport.DoubleHistogramHolder();

    private DoubleHistogram orderPayLatency() {
        return MetricsSupport.lazyHistogram(meter, orderPayLatency,
                "demo.order.pay.latency", "Order payment latency");
    }

    /**
     * 执行支付。
     *
     * @param orderId 订单 ID
     * @return 更新后的订单
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderVO execute(Long orderId) {
        long start = System.currentTimeMillis();
        log.info("Paying order: orderId={}", orderId);

        DemoOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw BizException.of(OrderErrorCode.ORDER_NOT_FOUND, "订单不存在: " + orderId);
        }
        if (order.getStatus() != OrderStatus.CREATED && order.getStatus() != OrderStatus.PENDING) {
            throw BizException.of(OrderErrorCode.ORDER_STATUS_NOT_PAYABLE,
                    String.format("订单状态不允许支付: orderId=%d, 当前状态=%s", orderId, order.getStatus()));
        }
        order.setStatus(OrderStatus.PAID);
        orderMapper.updateById(order);

        orderPayLatency().record(System.currentTimeMillis() - start);
        log.info("Order paid: orderId={}", orderId);
        return orderConvert.toVO(order);
    }
}
