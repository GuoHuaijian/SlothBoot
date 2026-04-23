package com.sloth.boot.starter.sms.core;

import com.sloth.boot.starter.sms.model.SendResult;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 短信模板门面。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class SmsTemplate {

    private final SmsClient smsClient;

    /**
     * 发送短信。
     *
     * @param phone        手机号
     * @param templateCode 模板编码
     * @param params       模板参数
     * @return 发送结果
     */
    public SendResult send(String phone, String templateCode, Map<String, String> params) {
        return smsClient.send(phone, templateCode, params);
    }

    /**
     * 批量发送短信。
     *
     * @param phones       手机号列表
     * @param templateCode 模板编码
     * @param params       模板参数
     * @return 发送结果
     */
    public SendResult batchSend(List<String> phones, String templateCode, Map<String, String> params) {
        return smsClient.batchSend(phones, templateCode, params);
    }
}
