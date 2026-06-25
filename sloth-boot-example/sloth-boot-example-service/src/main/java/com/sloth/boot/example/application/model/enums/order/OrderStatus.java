package com.sloth.boot.example.application.model.enums.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单状态枚举。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum OrderStatus {

    CREATED("CREATED", "已创建"),
    PAID("PAID", "已支付"),
    SHIPPED("SHIPPED", "已发货"),
    COMPLETED("COMPLETED", "已完成"),
    CANCELLED("CANCELLED", "已取消");

    private final String code;
    private final String description;

    /**
     * 根据状态码获取枚举。
     *
     * @param code 状态码
     * @return 订单状态枚举
     */
    public static OrderStatus fromCode(String code) {
        for (OrderStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的订单状态: " + code);
    }
}
