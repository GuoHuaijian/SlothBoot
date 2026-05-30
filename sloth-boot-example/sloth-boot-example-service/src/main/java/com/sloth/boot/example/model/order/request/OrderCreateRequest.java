package com.sloth.boot.example.model.order.request;

import lombok.Data;

@Data
public class OrderCreateRequest {

    private Long productId;

    private Integer quantity;
}
