package com.sloth.boot.starter.monitor.endpoint;

import com.sloth.boot.starter.monitor.util.MonitorUtil;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.core.env.Environment;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 应用信息端点。
 * <p>
 * 返回应用名称、版本、构建信息、部署环境、JDK、启动时间、主机信息等，
 * 用于部署验证和运行时诊断。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Endpoint(id = "appInfo")
public class InfoEndpoint {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

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

        // 应用基本信息
        info.put("name", environment.getProperty("spring.application.name", "application"));
        info.put("version", environment.getProperty("info.app.version", "unknown"));
        info.put("description", environment.getProperty("info.app.description", ""));
        info.put("springBootVersion", SpringBootVersion.getVersion());

        // 构建信息（配合 spring-boot-maven-plugin 的 build-info goal）
        info.put("buildTime", environment.getProperty("info.build.time", ""));
        info.put("buildGroup", environment.getProperty("info.build.group", ""));
        info.put("buildArtifact", environment.getProperty("info.build.artifact", ""));

        // Git 信息（配合 git-commit-id-plugin）
        info.put("gitCommitId", environment.getProperty("info.git.commit.id", ""));
        info.put("gitBranch", environment.getProperty("info.git.branch", ""));
        info.put("gitCommitTime", environment.getProperty("info.git.commit.time", ""));

        // 部署环境
        info.put("profiles", environment.getActiveProfiles().length > 0
                ? String.join(",", environment.getActiveProfiles())
                : environment.getDefaultProfiles() != null ? String.join(",", environment.getDefaultProfiles()) : "default");

        // 运行时信息
        info.put("startTime", FORMATTER.format(startTime));
        info.put("uptime", MonitorUtil.formatDuration(ManagementFactory.getRuntimeMXBean().getUptime()));

        // JDK 信息
        info.put("jdkVersion", System.getProperty("java.version"));
        info.put("jdkVendor", System.getProperty("java.vendor"));
        info.put("jvmName", System.getProperty("java.vm.name"));

        // 主机信息
        info.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        info.put("maxMemory", String.format("%.0f MB", Runtime.getRuntime().maxMemory() / (1024.0 * 1024.0)));
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            info.put("hostName", localHost.getHostName());
            info.put("hostAddress", localHost.getHostAddress());
        } catch (Exception ignored) {
            info.put("hostName", "unknown");
            info.put("hostAddress", "unknown");
        }
        info.put("osName", System.getProperty("os.name"));
        info.put("osArch", System.getProperty("os.arch"));

        return info;
    }
}
