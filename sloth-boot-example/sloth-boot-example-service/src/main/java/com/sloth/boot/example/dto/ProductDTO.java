package com.sloth.boot.example.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductDTO {

    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer stock;

    private String category;

    private LocalDateTime createTime;
}
