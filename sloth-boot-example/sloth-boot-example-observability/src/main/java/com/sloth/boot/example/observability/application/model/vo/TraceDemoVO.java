package com.sloth.boot.example.observability.application.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 链路追踪演示结果。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "链路追踪演示结果")
public class TraceDemoVO {

    @Schema(description = "链路 ID（无 OTel Agent 时为占位值）")
    private String traceId;

    @Schema(description = "Span ID（无 OTel Agent 时为占位值）")
    private String spanId;

    @Schema(description = "模拟耗时（毫秒）", example = "65")
    private int latencyMs;
}
