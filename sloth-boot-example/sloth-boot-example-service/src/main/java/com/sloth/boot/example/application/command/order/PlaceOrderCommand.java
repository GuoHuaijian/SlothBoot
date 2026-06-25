package com.sloth.boot.example.application.command.order;

import com.sloth.boot.common.context.UserContext;
import com.sloth.boot.common.exception.BizException;
import com.sloth.boot.example.application.command.redis.RedisDemoCommand;
import com.sloth.boot.example.application.helper.order.OrderAssembler;
import com.sloth.boot.example.application.model.enums.order.OrderErrorCode;
import com.sloth.boot.example.application.model.enums.order.OrderStatus;
import com.sloth.boot.example.application.model.form.order.OrderCreateForm;
import com.sloth.boot.example.infrastructure.model.po.order.DemoOrder;
import com.sloth.boot.example.infrastructure.model.po.product.Product;
import com.sloth.boot.example.infrastructure.repository.mapper.order.OrderMapper;
import com.sloth.boot.example.infrastructure.repository.mapper.product.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 创建订单命令。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PlaceOrderCommand {

    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;
    private final OrderAssembler orderAssembler;
    private final RedisDemoCommand redisDemoCommand;

    /**
     * 执行创建订单操作。
     * <p>
     * 根据商品ID和购买数量创建订单，包含以下业务逻辑：
     * 1. 验证用户已登录
     * 2. 查询商品信息并验证商品存在
     * 3. 验证商品库存是否充足
     * 4. 计算订单总价并持久化
     * 5. 发布订单创建事件
     *
     * @param form 订单创建表单
     * @return 创建成功的订单ID
     * @throws BizException 当用户未认证、商品不存在或库存不足时抛出
     */
    @Transactional(rollbackFor = Exception.class)
    public Long execute(OrderCreateForm form) {
        // 获取当前用户ID
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw BizException.of(OrderErrorCode.ORDER_USER_NOT_AUTHENTICATED);
        }

        // 查询商品信息
        Product product = productMapper.selectById(form.getProductId());
        if (product == null) {
            throw BizException.of(OrderErrorCode.ORDER_PRODUCT_NOT_FOUND,
                String.format("商品不存在: productId=%d", form.getProductId()));
        }
        if (product.getStock() < form.getQuantity()) {
            throw BizException.of(OrderErrorCode.ORDER_PRODUCT_OUT_OF_STOCK,
                String.format("商品库存不足: productId=%d, 当前库存=%d, 需求数量=%d",
                    form.getProductId(), product.getStock(), form.getQuantity()));
        }

        // 组装订单
        DemoOrder order = orderAssembler.assembleOrder(form, product, userId);
        orderMapper.insert(order);

        // 发布订单创建事件
        redisDemoCommand.publishOrderEvent(order.getId(), OrderStatus.CREATED.getCode(), "订单已创建");

        log.info("创建订单成功: orderId={}, userId={}, product={}, amount={}",
            order.getId(), userId, product.getName(), order.getTotalPrice());
        return order.getId();
    }
}
