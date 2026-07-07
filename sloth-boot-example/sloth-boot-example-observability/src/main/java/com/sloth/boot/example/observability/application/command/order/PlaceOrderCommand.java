package com.sloth.boot.example.observability.application.command.order;

import com.sloth.boot.common.exception.BizException;
import com.sloth.boot.common.result.R;
import com.sloth.boot.example.observability.application.model.convert.OrderConvert;
import com.sloth.boot.example.observability.application.model.enums.order.OrderErrorCode;
import com.sloth.boot.example.observability.application.model.enums.order.OrderStatus;
import com.sloth.boot.example.observability.application.model.form.order.PlaceOrderForm;
import com.sloth.boot.example.observability.application.model.vo.PlaceOrderResultVO;
import com.sloth.boot.example.observability.application.model.vo.ProductVO;
import com.sloth.boot.example.observability.application.model.vo.UserVO;
import com.sloth.boot.example.observability.infrastructure.model.po.order.DemoOrder;
import com.sloth.boot.example.observability.infrastructure.model.po.product.DemoProduct;
import com.sloth.boot.example.observability.infrastructure.repository.mapper.DemoOrderMapper;
import com.sloth.boot.example.observability.infrastructure.repository.mapper.DemoProductMapper;
import com.sloth.boot.example.observability.application.helper.MetricsSupport;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

/**
 * 下单命令（写操作）。
 * <p>
 * 通过 RestTemplate 自调 /users 与 /products 形成多跳调用链供 Tempo NodeGraph 展示，
 * 校验后持久化订单并记录下单计数与延迟指标。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PlaceOrderCommand {

    private final DemoOrderMapper orderMapper;
    private final DemoProductMapper productMapper;
    private final OrderConvert orderConvert;
    private final RestTemplate restTemplate;
    private final Meter meter;

    @Value("${server.port:8080}")
    private int serverPort;

    private final MetricsSupport.LongCounterHolder orderCreatedCounter = new MetricsSupport.LongCounterHolder();
    private final MetricsSupport.DoubleHistogramHolder orderCreateLatency = new MetricsSupport.DoubleHistogramHolder();

    private LongCounter orderCreatedCounter() {
        return MetricsSupport.lazyCounter(meter, orderCreatedCounter,
                "demo.orders.created", "Total number of orders created");
    }

    private DoubleHistogram orderCreateLatency() {
        return MetricsSupport.lazyHistogram(meter, orderCreateLatency,
                "demo.order.create.latency", "Order creation latency");
    }

    /**
     * 执行下单。
     *
     * @param form 下单表单
     * @return 下单结果
     */
    @Transactional(rollbackFor = Exception.class)
    public PlaceOrderResultVO execute(PlaceOrderForm form) {
        long start = System.currentTimeMillis();
        log.info("Placing order: userId={}, productId={}, quantity={}",
                form.getUserId(), form.getProductId(), form.getQuantity());

        // 自调用 HTTP 形成多跳 span（app -> /users -> /products），Tempo NodeGraph 可见
        String baseUrl = "http://localhost:" + serverPort;
        R<UserVO> userResp = restTemplate.exchange(baseUrl + "/api/demo/users/" + form.getUserId(),
                HttpMethod.GET, null, new ParameterizedTypeReference<R<UserVO>>() {}).getBody();
        R<ProductVO> productResp = restTemplate.exchange(baseUrl + "/api/demo/products/" + form.getProductId(),
                HttpMethod.GET, null, new ParameterizedTypeReference<R<ProductVO>>() {}).getBody();
        UserVO user = userResp != null ? userResp.getData() : null;
        ProductVO product = productResp != null ? productResp.getData() : null;
        if (user == null) {
            throw BizException.of(OrderErrorCode.ORDER_USER_NOT_FOUND,
                    String.format("用户不存在: userId=%d", form.getUserId()));
        }
        if (product == null) {
            throw BizException.of(OrderErrorCode.ORDER_PRODUCT_NOT_FOUND,
                    String.format("商品不存在: productId=%d", form.getProductId()));
        }

        DemoProduct productEntity = productMapper.selectById(form.getProductId());
        if (productEntity.getStock() < form.getQuantity()) {
            throw BizException.of(OrderErrorCode.ORDER_PRODUCT_OUT_OF_STOCK,
                    String.format("商品库存不足: productId=%d, 库存=%d, 需求=%d",
                            form.getProductId(), productEntity.getStock(), form.getQuantity()));
        }

        BigDecimal amount = productEntity.getPrice().multiply(BigDecimal.valueOf(form.getQuantity()));
        DemoOrder order = orderConvert.toEntity(form);
        order.setUserId(form.getUserId());
        order.setProductName(productEntity.getName());
        order.setAmount(amount);
        order.setStatus(OrderStatus.CREATED);
        order.setOrderNo("ORD" + System.currentTimeMillis());
        orderMapper.insert(order);

        orderCreatedCounter().add(1);
        long elapsed = System.currentTimeMillis() - start;
        orderCreateLatency().record(elapsed);
        log.info("Order placed: orderId={}, amount={}, latency={}ms", order.getId(), amount, elapsed);

        return orderConvert.composePlaceOrder(order.getId(), user, product, form.getQuantity(), amount, OrderStatus.CREATED);
    }
}
