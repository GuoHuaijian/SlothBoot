package com.sloth.boot.starter.sms.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;

/**
 * 短信发送指标。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
public class SmsMetrics {

    private final Counter sendTotal;
    private final Counter sendSuccess;
    private final Counter sendFailure;
    private final Timer sendDuration;

    public SmsMetrics(MeterRegistry registry) {
        this.sendTotal = Counter.builder("sms.send.total")
            .description("短信发送总次数").register(registry);
        this.sendSuccess = Counter.builder("sms.send.success")
            .description("短信发送成功次数").register(registry);
        this.sendFailure = Counter.builder("sms.send.failure")
            .description("短信发送失败次数").register(registry);
        this.sendDuration = Timer.builder("sms.send.duration")
            .description("短信发送耗时").register(registry);
    }
}
