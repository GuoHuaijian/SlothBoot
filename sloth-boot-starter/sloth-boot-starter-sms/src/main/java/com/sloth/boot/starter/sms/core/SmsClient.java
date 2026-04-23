package com.sloth.boot.starter.sms.core;

import com.sloth.boot.starter.sms.model.SendResult;

import java.util.List;
import java.util.Map;

/**
 * 短信客户端接口。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public interface SmsClient {

    /**
     * 发送短信。
     *
     * @param phone        手机号
     * @param templateCode 模板编码
     * @param params       模板参数
     * @return 发送结果
     */
    SendResult send(String phone, String templateCode, Map<String, String> params);

    /**
     * 批量发送短信。
     *
     * @param phones       手机号列表
     * @param templateCode 模板编码
     * @param params       模板参数
     * @return 发送结果
     */
    SendResult batchSend(List<String> phones, String templateCode, Map<String, String> params);
}
