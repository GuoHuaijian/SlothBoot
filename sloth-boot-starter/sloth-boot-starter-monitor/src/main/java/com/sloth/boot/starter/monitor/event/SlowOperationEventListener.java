package com.sloth.boot.starter.monitor.event;

import com.sloth.boot.common.log.event.SlowOperationEvent;
import com.sloth.boot.starter.monitor.alarm.AlarmMessage;
import com.sloth.boot.starter.monitor.alarm.AlarmService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 慢操作事件监听器。
 * <p>
 * 消费各模块发布的 {@link SlowOperationEvent}，记录 Micrometer 指标并触发告警。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Component
@ConditionalOnClass(name = "com.sloth.boot.common.log.event.SlowOperationEvent")
public class SlowOperationEventListener {

    private final MeterRegistry meterRegistry;
    private final AlarmService alarmService;

    /**
     * 构造方法。
     *
     * @param meterRegistry    指标注册中心
     * @param alarmServiceProvider 告警服务提供者（可选）
     */
    public SlowOperationEventListener(MeterRegistry meterRegistry,
                                      ObjectProvider<AlarmService> alarmServiceProvider) {
        this.meterRegistry = meterRegistry;
        this.alarmService = alarmServiceProvider.getIfAvailable();
    }

    /**
     * 处理慢操作事件。
     *
     * @param event 慢操作事件
     */
    @EventListener
    public void onSlowOperation(SlowOperationEvent event) {
        log.warn("[Monitor] 慢操作: type={}, cost={}ms, threshold={}ms, detail={}",
            event.getOperationType(), event.getCostTimeMs(), event.getThresholdMs(), event.getDetail());

        Timer.builder("sloth.slow.operation")
            .tag("type", event.getOperationType())
            .register(meterRegistry)
            .record(event.getCostTimeMs(), TimeUnit.MILLISECONDS);

        if (alarmService != null) {
            AlarmMessage alarmMessage = new AlarmMessage();
            alarmMessage.setTitle("慢操作告警");
            StringBuilder content = new StringBuilder();
            content.append("类型: ").append(event.getOperationType());
            content.append("\n详情: ").append(truncate(event.getDetail(), 500));
            content.append("\n耗时: ").append(event.getCostTimeMs()).append("ms");
            content.append("\n阈值: ").append(event.getThresholdMs()).append("ms");
            if (event.getContext() != null) {
                content.append("\n上下文: ").append(event.getContext());
            }
            alarmMessage.setContent(content.toString());
            alarmService.send(alarmMessage);
        }
    }

    /**
     * 截断字符串。
     *
     * @param s      原始字符串
     * @param maxLen 最大长度
     * @return 截断后的字符串
     */
    private String truncate(String s, int maxLen) {
        if (s == null) {
            return "";
        }
        if (s.length() <= maxLen) {
            return s;
        }
        return s.substring(0, maxLen) + "...";
    }
}
