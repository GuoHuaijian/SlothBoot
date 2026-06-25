package com.sloth.boot.example.application.query.order;

import com.sloth.boot.example.application.model.convert.order.OrderConvert;
import com.sloth.boot.example.application.model.vo.order.OrderVO;
import com.sloth.boot.example.infrastructure.model.po.order.DemoOrder;
import com.sloth.boot.example.infrastructure.repository.mapper.order.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * 订单列表查询。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class ListOrdersQuery {

    private final OrderMapper orderMapper;
    private final OrderConvert orderConvert;

    /**
     * 执行订单列表查询。
     * <p>
     * 查询所有订单并按创建时间倒序排列。
     *
     * @return 订单列表
     */
    public List<OrderVO> execute() {
        List<DemoOrder> orders = orderMapper.listOrder().stream()
            .sorted(Comparator.comparing(DemoOrder::getCreateTime, Comparator.nullsLast(Comparator.reverseOrder())))
            .toList();
        return orderConvert.toVOList(orders);
    }
}
