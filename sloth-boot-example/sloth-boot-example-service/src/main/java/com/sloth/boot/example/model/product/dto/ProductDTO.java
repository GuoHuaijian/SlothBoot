package com.sloth.boot.example.model.product.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品数据传输对象
 *
 * @author sloth-boot
 * @since 1.0.0
 */
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
