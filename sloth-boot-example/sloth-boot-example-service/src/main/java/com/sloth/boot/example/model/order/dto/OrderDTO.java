package com.sloth.boot.example.model.order.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单数据传输对象
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
public class OrderDTO {

    private Long id;

    private Long userId;

    private Long productId;

    private String productName;

    private BigDecimal amount;

    private Integer quantity;

    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
