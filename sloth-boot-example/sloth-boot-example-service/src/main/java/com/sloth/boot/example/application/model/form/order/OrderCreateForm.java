package com.sloth.boot.example.application.model.form.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建订单表单。
 * <p>
 * 用于接收前端创建订单的请求参数，支持参数校验。
 * 创建订单时会自动获取当前登录用户ID。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Schema(description = "创建订单请求")
public class OrderCreateForm {

    /** 商品ID（必填） */
    @NotNull(message = "商品ID不能为空")
    @Schema(description = "商品ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productId;

    /** 购买数量（必填，最少1件） */
    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量最少为1")
    @Schema(description = "购买数量", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;
}
