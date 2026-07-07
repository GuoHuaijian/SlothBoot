package com.sloth.boot.example.observability.application.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 压测结果。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "压测结果")
public class LoadTestResultVO {

    @Schema(description = "总请求数", example = "50")
    private int total;

    @Schema(description = "成功请求数", example = "48")
    private int success;

    @Schema(description = "失败请求数", example = "2")
    private int error;

    @Schema(description = "总耗时（毫秒）", example = "3200")
    private long elapsedMs;

    @Schema(description = "平均单请求耗时（毫秒）", example = "64")
    private long avgLatencyMs;
}
