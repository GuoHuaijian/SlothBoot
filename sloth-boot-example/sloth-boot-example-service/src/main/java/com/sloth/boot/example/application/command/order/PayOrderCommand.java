package com.sloth.boot.example.application.command.order;

import com.sloth.boot.common.exception.BizException;
import com.sloth.boot.example.application.command.redis.RedisDemoCommand;
import com.sloth.boot.example.application.model.enums.order.OrderErrorCode;
import com.sloth.boot.example.application.model.enums.order.OrderStatus;
import com.sloth.boot.example.infrastructure.model.po.order.DemoOrder;
import com.sloth.boot.example.infrastructure.repository.mapper.order.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 支付订单命令。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PayOrderCommand {

    private final OrderMapper orderMapper;
    private final RedisDemoCommand redisDemoCommand;

    /**
     * 执行订单支付操作。
     * <p>
     * 将订单状态更新为已支付，并发布支付成功事件。
     *
     * @param orderId 订单ID
     * @throws BizException 当订单不存在时抛出
     */
    @Transactional(rollbackFor = Exception.class)
    public void execute(Long orderId) {
        DemoOrder order = orderMapper.selectById(orderId);
        if (order == null) throw BizException.of(OrderErrorCode.ORDER_NOT_FOUND);
        order.setStatus(OrderStatus.PAID);
        orderMapper.updateById(order);
        redisDemoCommand.publishOrderEvent(orderId, OrderStatus.PAID.getCode(), "订单已支付");
        log.info("订单支付成功: orderId={}", orderId);
    }
}
