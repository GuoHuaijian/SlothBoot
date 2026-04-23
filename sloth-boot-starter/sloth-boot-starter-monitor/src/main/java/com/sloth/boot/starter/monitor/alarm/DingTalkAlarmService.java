package com.sloth.boot.starter.monitor.alarm;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.StrUtil;
import com.sloth.boot.starter.monitor.config.MonitorProperties;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 钉钉告警服务。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class DingTalkAlarmService extends AbstractWebhookAlarmService {

    /**
     * 构造函数。
     *
     * @param restTemplate      RestTemplate
     * @param monitorProperties 监控配置
     */
    public DingTalkAlarmService(RestTemplate restTemplate, MonitorProperties monitorProperties) {
        super(restTemplate, monitorProperties);
    }

    @Override
    protected String buildWebhookUrl(String webhook, AlarmMessage message) {
        String secret = monitorProperties.getAlarm().getSecret();
        if (StrUtil.isBlank(secret)) {
            return webhook;
        }
        long timestamp = System.currentTimeMillis();
        String sign = sign(secret, timestamp);
        return UrlBuilder.ofHttp(webhook)
                .addQuery("timestamp", String.valueOf(timestamp))
                .addQuery("sign", sign)
                .build();
    }

    @Override
    protected Object buildPayload(AlarmMessage message) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("msgtype", "markdown");
        Map<String, Object> markdown = new HashMap<>();
        markdown.put("title", message.getTitle());
        markdown.put("text", "### " + message.getTitle() + "\n"
                + "> 级别: " + message.getLevel() + "\n\n"
                + "> 时间: " + message.getTime() + "\n\n"
                + "> 服务: " + message.getServiceName() + "\n\n"
                + "> IP: " + message.getIp() + "\n\n"
                + "> TraceId: " + message.getTraceId() + "\n\n"
                + message.getContent());
        payload.put("markdown", markdown);
        return payload;
    }

    private String sign(String secret, long timestamp) {
        try {
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            return URLEncoder.encode(Base64.encode(signData), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new IllegalStateException("生成钉钉签名失败", ex);
        }
    }
}
