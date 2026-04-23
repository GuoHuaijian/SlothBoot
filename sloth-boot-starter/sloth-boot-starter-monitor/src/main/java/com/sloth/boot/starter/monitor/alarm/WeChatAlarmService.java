package com.sloth.boot.starter.monitor.alarm;

import com.sloth.boot.starter.monitor.config.MonitorProperties;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 企业微信告警服务。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class WeChatAlarmService extends AbstractWebhookAlarmService {

    /**
     * 构造函数。
     *
     * @param restTemplate      RestTemplate
     * @param monitorProperties 监控配置
     */
    public WeChatAlarmService(RestTemplate restTemplate, MonitorProperties monitorProperties) {
        super(restTemplate, monitorProperties);
    }

    @Override
    protected Object buildPayload(AlarmMessage message) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("msgtype", "markdown");
        Map<String, Object> markdown = new HashMap<>();
        markdown.put("content", "## " + message.getTitle() + "\n"
                + "> 级别: " + message.getLevel() + "\n"
                + "> 时间: " + message.getTime() + "\n"
                + "> 服务: " + message.getServiceName() + "\n"
                + "> IP: " + message.getIp() + "\n"
                + "> TraceId: " + message.getTraceId() + "\n"
                + message.getContent());
        payload.put("markdown", markdown);
        return payload;
    }
}
