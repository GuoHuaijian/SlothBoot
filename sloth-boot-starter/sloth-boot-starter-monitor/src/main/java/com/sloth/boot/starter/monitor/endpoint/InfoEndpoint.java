package com.sloth.boot.starter.monitor.endpoint;

import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.core.env.Environment;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 应用信息端点。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Endpoint(id = "appInfo")
public class InfoEndpoint {

    private final Environment environment;
    private final Instant startTime = Instant.now();

    /**
     * 构造函数。
     *
     * @param environment Spring 环境
     */
    public InfoEndpoint(Environment environment) {
        this.environment = environment;
    }

    /**
     * 读取应用信息。
     *
     * @return 应用信息
     */
    @ReadOperation
    public Map<String, Object> info() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("name", environment.getProperty("spring.application.name", "application"));
        info.put("version", environment.getProperty("info.app.version", "unknown"));
        info.put("startTime", startTime);
        info.put("uptime", Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime()).toString());
        info.put("jdkVersion", System.getProperty("java.version"));
        info.put("springBootVersion", SpringBootVersion.getVersion());
        return info;
    }
}
