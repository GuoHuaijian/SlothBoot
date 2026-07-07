package com.sloth.boot.example.observability.application.query;

import com.sloth.boot.common.exception.BizException;
import com.sloth.boot.example.observability.application.model.convert.OrderConvert;
import com.sloth.boot.example.observability.application.model.enums.order.OrderErrorCode;
import com.sloth.boot.example.observability.application.model.vo.OrderVO;
import com.sloth.boot.example.observability.infrastructure.model.po.order.DemoOrder;
import com.sloth.boot.example.observability.infrastructure.repository.mapper.DemoOrderMapper;
import com.sloth.boot.example.observability.application.helper.MetricsSupport;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.Meter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 订单详情查询（读操作）。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GetOrderQuery {

    private final DemoOrderMapper orderMapper;
    private final OrderConvert orderConvert;
    private final Meter meter;

    private final MetricsSupport.DoubleHistogramHolder orderQueryLatency = new MetricsSupport.DoubleHistogramHolder();

    private DoubleHistogram orderQueryLatency() {
        return MetricsSupport.lazyHistogram(meter, orderQueryLatency,
                "demo.order.query.latency", "Order query latency");
    }

    /**
     * 执行订单详情查询。
     *
     * @param id 订单 ID
     * @return 订单视图对象
     */
    public OrderVO execute(Long id) {
        log.info("Querying order: orderId={}", id);
        int delay = ThreadLocalRandom.current().nextInt(10, 80);
        sleep(delay);
        DemoOrder order = orderMapper.selectById(id);
        orderQueryLatency().record(delay);
        if (order == null) {
            throw BizException.of(OrderErrorCode.ORDER_NOT_FOUND, "订单不存在: " + id);
        }
        return orderConvert.toVO(order);
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
