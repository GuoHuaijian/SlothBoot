package com.sloth.boot.example.adapter.controller.order;

import com.sloth.boot.common.result.R;
import com.sloth.boot.example.application.model.vo.order.OrderVO;
import com.sloth.boot.example.application.query.order.ListOrdersQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 订单列表查询接口。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Tag(name = "订单管理")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class ListOrdersController {

    private final ListOrdersQuery orderListQuery;

    @Operation(summary = "订单列表")
    @GetMapping("/list")
    public R<List<OrderVO>> execute() {
        return R.ok(orderListQuery.execute());
    }
}
