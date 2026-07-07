package com.sloth.boot.example.observability.adapter.controller;

import com.sloth.boot.common.result.R;
import com.sloth.boot.common.result.PageResult;
import com.sloth.boot.example.observability.application.command.order.PayOrderCommand;
import com.sloth.boot.example.observability.application.command.order.PlaceOrderCommand;
import com.sloth.boot.example.observability.application.model.form.order.PlaceOrderForm;
import com.sloth.boot.example.observability.application.model.vo.OrderVO;
import com.sloth.boot.example.observability.application.model.vo.PlaceOrderResultVO;
import com.sloth.boot.example.observability.application.query.GetOrderQuery;
import com.sloth.boot.example.observability.application.query.ListOrdersQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单演示接口。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Tag(name = "订单演示", description = "订单级联下单、支付、分页查询，演示业务指标与跨端点调用链")
@RestController
@RequestMapping("/api/demo/orders")
@RequiredArgsConstructor
public class OrderController {

    private final PlaceOrderCommand placeOrderCommand;
    private final PayOrderCommand payOrderCommand;
    private final ListOrdersQuery listOrdersQuery;
    private final GetOrderQuery getOrderQuery;

    @Operation(summary = "级联下单", description = "通过自调用 /users 与 /products 形成多跳调用链，生成订单并记录创建指标")
    @PostMapping("/place")
    public R<PlaceOrderResultVO> placeOrder(@Valid @RequestBody PlaceOrderForm form) {
        return R.ok(placeOrderCommand.execute(form));
    }

    @Operation(summary = "支付订单", description = "将待支付订单置为 PAID")
    @Parameter(name = "id", description = "订单 ID", required = true, example = "1001")
    @PostMapping("/{id}/pay")
    public R<OrderVO> pay(@PathVariable Long id) {
        return R.ok(payOrderCommand.execute(id));
    }

    @Operation(summary = "分页查询订单", description = "按创建时间倒序分页查询，记录查询延迟指标")
    @Parameter(name = "page", description = "页码（1 起）", example = "1")
    @Parameter(name = "size", description = "每页大小", example = "10")
    @Parameter(name = "status", description = "可选状态过滤", example = "PAID")
    @GetMapping
    public R<PageResult<OrderVO>> list(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       @RequestParam(required = false) String status) {
        return R.ok(listOrdersQuery.execute(page, size, status));
    }

    @Operation(summary = "查询订单详情", description = "查询指定订单并记录查询延迟指标")
    @Parameter(name = "id", description = "订单 ID", required = true, example = "1001")
    @GetMapping("/{id}")
    public R<OrderVO> get(@PathVariable Long id) {
        return R.ok(getOrderQuery.execute(id));
    }
}
