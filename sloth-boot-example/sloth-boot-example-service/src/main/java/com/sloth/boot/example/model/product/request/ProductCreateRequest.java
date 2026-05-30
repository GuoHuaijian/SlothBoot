package com.sloth.boot.example.model.product.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductCreateRequest {

    private String name;

    private String description;

    private BigDecimal price;

    private Integer stock;

    private String category;
}
