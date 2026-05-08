package com.sloth.boot.example.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.sloth.boot.common.annotation.DistributedLock;
import com.sloth.boot.common.annotation.Idempotent;
import com.sloth.boot.common.annotation.OperateLog;
import com.sloth.boot.common.annotation.RateLimit;
import com.sloth.boot.common.enums.OperateTypeEnum;
import com.sloth.boot.common.result.R;
import com.sloth.boot.example.dto.*;
import com.sloth.boot.example.service.OrderDemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderDemoService orderService;

    @PostMapping("/create")
    @DistributedLock(key = "'order:create:' + #request.productId", waitTime = 5, leaseTime = 30, message = "下单处理中，请勿重复操作")
    @Idempotent(timeout = 30, message = "请勿重复提交订单")
    public R<OrderDTO> createOrder(@RequestBody OrderCreateRequest request) {
        long userId = StpUtil.isLogin() ? StpUtil.getLoginIdAsLong() : 1L;
        return R.ok(orderService.createOrder(request, userId));
    }

    @GetMapping("/{id}")
    public R<OrderDTO> getOrder(@PathVariable Long id) {
        OrderDTO order = orderService.getOrder(id);
        if (order == null) {
            return R.fail("订单不存在");
        }
        return R.ok(order);
    }

    @PutMapping("/{id}/pay")
    @DistributedLock(key = "'order:' + #id", waitTime = 5, leaseTime = 30, message = "支付处理中")
    public R<OrderDTO> payOrder(@PathVariable Long id) {
        return R.ok(orderService.payOrder(id));
    }

    @PutMapping("/{id}/cancel")
    @OperateLog(module = "订单管理", description = "取消订单", type = OperateTypeEnum.UPDATE)
    public R<OrderDTO> cancelOrder(@PathVariable Long id) {
        return R.ok(orderService.cancelOrder(id));
    }

    @GetMapping("/list")
    @OperateLog(module = "订单管理", description = "查询订单列表", type = OperateTypeEnum.QUERY)
    public R<List<OrderDTO>> listOrders() {
        return R.ok(orderService.listOrders());
    }

    @GetMapping("/rate-limit-test")
    @RateLimit(count = 5, period = 10, message = "10秒内最多5次请求")
    public R<String> rateLimitTest() {
        return R.ok("请求成功");
    }

    @GetMapping("/events")
    public R<List<OrderStatusEvent>> getRecentEvents(@RequestParam(defaultValue = "20") int count) {
        return R.ok(orderService.getRecentEvents(count));
    }
}
