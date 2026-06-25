package com.sloth.boot.example.adapter.controller.order;

import com.sloth.boot.common.log.annotation.OperateLog;
import com.sloth.boot.common.log.annotation.OperateTypeEnum;
import com.sloth.boot.common.result.R;
import com.sloth.boot.example.application.command.order.PayOrderCommand;
import com.sloth.boot.starter.redis.annotation.DistributedLock;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 支付订单接口。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Tag(name = "订单管理")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class PayOrderController {

    private final PayOrderCommand payOrderCommand;

    @Operation(summary = "支付订单")
    @OperateLog(module = "订单管理", description = "支付订单", type = OperateTypeEnum.UPDATE)
    @DistributedLock(key = "#orderId", waitTime = 3, leaseTime = 10)
    @PutMapping("/{id}/pay")
    public R<Void> pay(@PathVariable("id") Long orderId) {
        payOrderCommand.execute(orderId);
        return R.ok();
    }
}
