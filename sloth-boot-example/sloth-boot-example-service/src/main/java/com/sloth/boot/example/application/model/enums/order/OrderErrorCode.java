package com.sloth.boot.example.application.model.enums.order;

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

    ORDER_NOT_FOUND(100401, "订单不存在"),
    ORDER_PRODUCT_NOT_FOUND(100402, "商品不存在"),
    ORDER_PRODUCT_OUT_OF_STOCK(100403, "商品库存不足"),
    ORDER_USER_NOT_AUTHENTICATED(100404, "用户未认证，请先登录");

    private final int code;
    private final String msg;
}
