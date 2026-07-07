package com.sloth.boot.example.observability.adapter.controller;

import com.sloth.boot.common.result.R;
import com.sloth.boot.example.observability.application.command.DemoCommand;
import com.sloth.boot.example.observability.application.model.vo.MetricsDemoVO;
import com.sloth.boot.example.observability.application.model.vo.SlowOperationVO;
import com.sloth.boot.example.observability.application.model.vo.TraceDemoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 可观测性演示接口。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Tag(name = "可观测性演示", description = "慢操作、异常、链路追踪、自定义指标演示")
@RestController
@RequestMapping("/api/demo")
@RequiredArgsConstructor
public class DemoController {

    private final DemoCommand demoCommand;

    @Operation(summary = "慢操作", description = "模拟 2500–3500ms 的慢请求，用于触发慢请求告警与延迟分布观测")
    @GetMapping("/slow")
    public R<SlowOperationVO> slow() {
        return R.ok(demoCommand.slow());
    }

    @Operation(summary = "模拟业务异常", description = "抛出异常并递增 demo.errors 计数器，演示异常链路与错误指标")
    @GetMapping("/error")
    public R<Void> error() {
        demoCommand.error();
        return R.ok();
    }

    @Operation(summary = "链路追踪演示", description = "演示父 span 与异步子 span 的 traceId 上下文透传")
    @GetMapping("/trace")
    public R<TraceDemoVO> trace() {
        return R.ok(demoCommand.trace());
    }

    @Operation(summary = "自定义指标演示", description = "递增 demo.custom.metric 计数器并记录 demo.timer.processing 直方图")
    @GetMapping("/metrics")
    public R<MetricsDemoVO> metrics() {
        return R.ok(demoCommand.metrics());
    }
}
