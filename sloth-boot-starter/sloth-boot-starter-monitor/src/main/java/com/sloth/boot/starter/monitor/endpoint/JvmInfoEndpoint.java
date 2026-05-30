package com.sloth.boot.starter.monitor.endpoint;

import com.sloth.boot.starter.monitor.model.JvmInfo;
import com.sloth.boot.starter.monitor.service.JvmInfoService;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

/**
 * JVM 信息端点，暴露 {@code /actuator/jvmInfo}。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Endpoint(id = "jvmInfo")
public class JvmInfoEndpoint {

    private final JvmInfoService jvmInfoService;

    /**
     * 构造函数。
     *
     * @param jvmInfoService JVM 信息采集服务
     */
    public JvmInfoEndpoint(JvmInfoService jvmInfoService) {
        this.jvmInfoService = jvmInfoService;
    }

    /**
     * 读取 JVM 信息。
     *
     * @return JVM 信息
     */
    @ReadOperation
    public JvmInfo jvmInfo() {
        return jvmInfoService.getJvmInfo();
    }
}
