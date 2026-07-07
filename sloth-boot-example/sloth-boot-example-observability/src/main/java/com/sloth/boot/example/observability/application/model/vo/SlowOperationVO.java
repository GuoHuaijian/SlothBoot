package com.sloth.boot.example.observability.application.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 慢操作演示结果。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "慢操作演示结果")
public class SlowOperationVO {

    @Schema(description = "模拟耗时（毫秒）", example = "3000")
    private int latencyMs;

    @Schema(description = "说明信息", example = "This endpoint simulates a slow operation")
    private String message;
}
