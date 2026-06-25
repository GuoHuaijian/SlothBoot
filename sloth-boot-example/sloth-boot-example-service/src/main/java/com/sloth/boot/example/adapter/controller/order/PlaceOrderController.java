package com.sloth.boot.example.adapter.controller.order;

import com.sloth.boot.common.log.annotation.OperateLog;
import com.sloth.boot.common.log.annotation.OperateTypeEnum;
import com.sloth.boot.common.result.R;
import com.sloth.boot.example.application.command.order.PlaceOrderCommand;
import com.sloth.boot.example.application.model.form.order.OrderCreateForm;
import com.sloth.boot.starter.idempotent.annotation.Idempotent;
import com.sloth.boot.starter.redis.annotation.DistributedLock;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 创建订单接口。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Tag(name = "订单管理")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class PlaceOrderController {

    private final PlaceOrderCommand placeOrderCommand;

    @Operation(summary = "创建订单")
    @OperateLog(module = "订单管理", description = "创建订单", type = OperateTypeEnum.CREATE)
    @DistributedLock(key = "#form.productId", waitTime = 3, leaseTime = 10)
    @Idempotent(timeout = 30, message = "请勿重复提交订单")
    @PostMapping
    public R<Long> place(@Valid @RequestBody OrderCreateForm form) {
        return R.ok(placeOrderCommand.execute(form));
    }
}
