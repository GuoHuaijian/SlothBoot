package com.sloth.boot.example.observability.application.model.form.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 下单表单。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Schema(description = "下单请求")
public class PlaceOrderForm {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @NotNull(message = "商品ID不能为空")
    @Schema(description = "商品ID", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productId;

    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量最少为1")
    @Schema(description = "购买数量", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;
}
