package com.sloth.boot.example.observability.application.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自定义指标演示结果。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "自定义指标演示结果")
public class MetricsDemoVO {

    @Schema(description = "递增的计数器名称", example = "demo.custom.metric")
    private String counter;

    @Schema(description = "记录的直方图名称", example = "demo.timer.processing")
    private String histogram;
}
