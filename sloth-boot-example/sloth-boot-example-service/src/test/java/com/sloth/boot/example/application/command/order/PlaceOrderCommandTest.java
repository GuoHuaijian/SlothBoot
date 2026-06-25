package com.sloth.boot.example.application.command.order;

import com.sloth.boot.common.context.UserContext;
import com.sloth.boot.common.exception.BizException;
import com.sloth.boot.example.application.model.enums.order.OrderErrorCode;
import com.sloth.boot.example.application.model.enums.order.OrderStatus;
import com.sloth.boot.example.application.helper.order.OrderAssembler;
import com.sloth.boot.example.application.model.form.order.OrderCreateForm;
import com.sloth.boot.example.infrastructure.model.po.order.DemoOrder;
import com.sloth.boot.example.infrastructure.model.po.product.Product;
import com.sloth.boot.example.infrastructure.repository.mapper.order.OrderMapper;
import com.sloth.boot.example.infrastructure.repository.mapper.product.ProductMapper;
import com.sloth.boot.example.application.command.redis.RedisDemoCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 创建订单命令单元测试。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class PlaceOrderCommandTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private OrderAssembler orderAssembler;

    @Mock
    private RedisDemoCommand redisDemoCommand;

    @InjectMocks
    private PlaceOrderCommand placeOrderCommand;

    private UserContext.UserInfo userInfo;

    @BeforeEach
    void setUp() {
        // 设置测试用户上下文
        userInfo = new UserContext.UserInfo();
        userInfo.setUserId(1001L);
        userInfo.setUsername("testuser");
        UserContext.set(userInfo);
    }

    @Test
    void testPlaceOrderSuccess() {
        // 准备测试数据
        OrderCreateForm form = new OrderCreateForm();
        form.setProductId(100L);
        form.setQuantity(2);

        Product product = new Product();
        product.setId(100L);
        product.setName("测试商品");
        product.setPrice(BigDecimal.valueOf(99.99));
        product.setStock(10);

        DemoOrder order = new DemoOrder();
        order.setId(1L);
        order.setUserId(1001L);
        order.setProductId(100L);
        order.setProductName("测试商品");
        order.setQuantity(2);
        order.setTotalPrice(BigDecimal.valueOf(199.98));
        order.setStatus(OrderStatus.CREATED);

        when(productMapper.selectById(100L)).thenReturn(product);
        when(orderAssembler.assembleOrder(form, product, 1001L)).thenReturn(order);
        when(orderMapper.insert(any(DemoOrder.class))).thenReturn(1);

        // 执行测试
        Long orderId = placeOrderCommand.execute(form);

        // 验证结果
        assertEquals(1L, orderId);
        assertEquals(OrderStatus.CREATED, order.getStatus());
        assertEquals(1001L, order.getUserId());
        verify(productMapper).selectById(100L);
        verify(orderAssembler).assembleOrder(form, product, 1001L);
        verify(orderMapper).insert(order);
        verify(redisDemoCommand).publishOrderEvent(eq(1L), eq(OrderStatus.CREATED.getCode()), eq("订单已创建"));
    }

    @Test
    void testPlaceOrderUserNotAuthenticated() {
        // 清除用户上下文
        UserContext.clear();

        OrderCreateForm form = new OrderCreateForm();
        form.setProductId(100L);
        form.setQuantity(1);

        // 执行测试并验证异常
        BizException exception = assertThrows(BizException.class, () -> placeOrderCommand.execute(form));
        assertEquals(OrderErrorCode.ORDER_USER_NOT_AUTHENTICATED.getCode(), exception.getCode());
    }

    @Test
    void testPlaceOrderProductNotFound() {
        OrderCreateForm form = new OrderCreateForm();
        form.setProductId(999L);
        form.setQuantity(1);

        when(productMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        BizException exception = assertThrows(BizException.class, () -> placeOrderCommand.execute(form));
        assertEquals(OrderErrorCode.ORDER_PRODUCT_NOT_FOUND.getCode(), exception.getCode());
        assertTrue(exception.getMessage().contains("999"));
    }

    @Test
    void testPlaceOrderProductOutOfStock() {
        OrderCreateForm form = new OrderCreateForm();
        form.setProductId(100L);
        form.setQuantity(1);

        Product product = new Product();
        product.setId(100L);
        product.setName("测试商品");
        product.setPrice(BigDecimal.valueOf(99.99));
        product.setStock(0); // 库存为0

        when(productMapper.selectById(100L)).thenReturn(product);

        // 执行测试并验证异常
        BizException exception = assertThrows(BizException.class, () -> placeOrderCommand.execute(form));
        assertEquals(OrderErrorCode.ORDER_PRODUCT_OUT_OF_STOCK.getCode(), exception.getCode());
        assertTrue(exception.getMessage().contains("库存不足"));
    }

    @Test
    void testPlaceOrderInsufficientStock() {
        OrderCreateForm form = new OrderCreateForm();
        form.setProductId(100L);
        form.setQuantity(5); // 购买5个

        Product product = new Product();
        product.setId(100L);
        product.setName("测试商品");
        product.setPrice(BigDecimal.valueOf(99.99));
        product.setStock(3); // 只有3个库存

        when(productMapper.selectById(100L)).thenReturn(product);

        // 执行测试并验证异常
        BizException exception = assertThrows(BizException.class, () -> placeOrderCommand.execute(form));
        assertEquals(OrderErrorCode.ORDER_PRODUCT_OUT_OF_STOCK.getCode(), exception.getCode());
        assertTrue(exception.getMessage().contains("3"));
    }
}
