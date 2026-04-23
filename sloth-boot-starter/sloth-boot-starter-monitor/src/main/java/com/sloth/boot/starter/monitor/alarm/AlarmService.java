package com.sloth.boot.starter.monitor.alarm;

/**
 * 告警服务接口。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public interface AlarmService {

    /**
     * 发送告警。
     *
     * @param title   标题
     * @param content 内容
     */
    void send(String title, String content);

    /**
     * 发送告警。
     *
     * @param message 告警消息
     */
    void send(AlarmMessage message);
}
