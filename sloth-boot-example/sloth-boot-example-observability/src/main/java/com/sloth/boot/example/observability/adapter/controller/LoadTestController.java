package com.sloth.boot.example.observability.adapter.controller;

import com.sloth.boot.common.result.R;
import com.sloth.boot.example.observability.application.command.LoadTestCommand;
import com.sloth.boot.example.observability.application.model.vo.LoadTestResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 压测演示接口。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Tag(name = "压测演示", description = "并发调用各演示端点，批量产生可观测数据")
@RestController
@RequestMapping("/api/demo/load-test")
@RequiredArgsConstructor
public class LoadTestController {

    private final LoadTestCommand loadTestCommand;

    @Operation(summary = "执行压测", description = "并发调用各演示端点，用于在可观测性面板上批量产生指标、链路与日志数据")
    @Parameter(name = "count", description = "请求数量", example = "50")
    @PostMapping
    public R<LoadTestResultVO> runLoadTest(@RequestParam(defaultValue = "50") int count) {
        return R.ok(loadTestCommand.run(count));
    }
}
