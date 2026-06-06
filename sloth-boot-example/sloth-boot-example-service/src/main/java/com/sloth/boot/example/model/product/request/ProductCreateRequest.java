package com.sloth.boot.example.model.product.request;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品创建请求参数
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
public class ProductCreateRequest {

    private String name;

    private String description;

    private BigDecimal price;

    private Integer stock;

    private String category;
}
