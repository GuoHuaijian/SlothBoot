package com.sloth.boot.example.controller;

import com.sloth.boot.common.result.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查控制器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@RestController
public class HealthController {

    /**
     * 健康检查接口。
     *
     * @return 健康状态
     */
    @GetMapping("/health")
    public R<String> health() {
        return R.ok("UP");
    }
}
