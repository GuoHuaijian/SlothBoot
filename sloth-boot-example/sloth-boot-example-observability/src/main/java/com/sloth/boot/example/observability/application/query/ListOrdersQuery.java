package com.sloth.boot.example.observability.application.query;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sloth.boot.common.result.PageResult;
import com.sloth.boot.example.observability.application.model.convert.OrderConvert;
import com.sloth.boot.example.observability.application.model.vo.OrderVO;
import com.sloth.boot.example.observability.infrastructure.model.po.order.DemoOrder;
import com.sloth.boot.example.observability.infrastructure.repository.mapper.DemoOrderMapper;
import com.sloth.boot.example.observability.application.helper.MetricsSupport;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.Meter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 订单分页查询（读操作）。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ListOrdersQuery {

    private final DemoOrderMapper orderMapper;
    private final OrderConvert orderConvert;
    private final Meter meter;

    private final MetricsSupport.DoubleHistogramHolder orderQueryLatency = new MetricsSupport.DoubleHistogramHolder();

    private DoubleHistogram orderQueryLatency() {
        return MetricsSupport.lazyHistogram(meter, orderQueryLatency,
                "demo.order.query.latency", "Order query latency");
    }

    /**
     * 执行订单分页查询。
     *
     * @param pageNum 页码（1 起）
     * @param pageSize 每页大小
     * @param status  可选状态过滤
     * @return 分页结果
     */
    public PageResult<OrderVO> execute(int pageNum, int pageSize, String status) {
        log.info("Listing orders: page={}, size={}, status={}", pageNum, pageSize, status);
        long start = System.currentTimeMillis();
        Page<DemoOrder> page = new Page<>(pageNum, pageSize);
        IPage<DemoOrder> result = orderMapper.selectPageOrder(page, status);
        PageResult<OrderVO> pageResult = PageResult.of(
                orderConvert.toVOList(result.getRecords()),
                result.getTotal(),
                (int) result.getCurrent(),
                (int) result.getSize());
        orderQueryLatency().record(System.currentTimeMillis() - start);
        return pageResult;
    }
}
