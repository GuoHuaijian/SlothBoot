package com.sloth.boot.example.observability.application.model.enums.order;

import com.sloth.boot.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单模块错误码。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum OrderErrorCode implements ErrorCode {

    ORDER_NOT_FOUND(200401, "订单不存在"),
    ORDER_STATUS_NOT_PAYABLE(200402, "订单状态不允许支付"),
    ORDER_USER_NOT_FOUND(200403, "用户不存在"),
    ORDER_PRODUCT_NOT_FOUND(200404, "商品不存在"),
    ORDER_PRODUCT_OUT_OF_STOCK(200405, "商品库存不足");

    private final int code;
    private final String msg;
}
