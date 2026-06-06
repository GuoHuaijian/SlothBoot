package com.sloth.boot.example.model.order.request;

import lombok.Data;

/**
 * 订单创建请求参数
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
public class OrderCreateRequest {

    private Long productId;

    private Integer quantity;
}
