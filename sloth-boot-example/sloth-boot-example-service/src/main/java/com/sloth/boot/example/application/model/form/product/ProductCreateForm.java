package com.sloth.boot.example.application.model.form.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建商品表单。
 * <p>
 * 用于接收前端创建商品的请求参数，支持参数校验。
 * 商品描述会自动进行XSS清洗。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Schema(description = "创建商品请求")
public class ProductCreateForm {

    /** 商品名称（必填） */
    @NotBlank(message = "商品名称不能为空")
    @Schema(description = "商品名称", example = "Sloth Boot 企业版", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /** 商品描述 */
    @Schema(description = "商品描述", example = "企业级脚手架框架")
    private String description;

    /** 商品价格（必填） */
    @Schema(description = "商品价格", example = "9999.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal price;

    /** 库存数量（必填） */
    @Schema(description = "库存数量", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer stock;
}
