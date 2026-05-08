package com.sloth.boot.example.dto;

import lombok.Data;

@Data
public class OrderCreateRequest {

    private Long productId;

    private Integer quantity;
}
