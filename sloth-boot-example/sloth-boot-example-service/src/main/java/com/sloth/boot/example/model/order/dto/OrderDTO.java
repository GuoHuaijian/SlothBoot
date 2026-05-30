package com.sloth.boot.example.model.order.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
