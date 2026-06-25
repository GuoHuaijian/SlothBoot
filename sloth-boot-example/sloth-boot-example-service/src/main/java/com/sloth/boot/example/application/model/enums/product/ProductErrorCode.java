package com.sloth.boot.example.application.model.enums.product;

import com.sloth.boot.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 商品模块错误码。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum ProductErrorCode implements ErrorCode {

    PRODUCT_NOT_FOUND(100301, "商品不存在"),
    PRODUCT_OUT_OF_STOCK(100302, "商品库存不足");

    private final int code;
    private final String msg;
}
