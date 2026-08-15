package com.sloth.boot.starter.rocketmq.event;

/**
 * 消息消费失败日志事件。
 * <p>
 * 当消息消费超过最大重试次数后发布，业务方可监听此事件实现失败日志持久化。
 *
 * @param topic          消息 Topic
 * @param tag            消息 Tag
 * @param msgId          消息 ID
 * @param msgKey         消息 Key
 * @param body           消息体
 * @param consumerGroup  消费组
 * @param retryTimes     已重试次数
 * @param errorMessage   最后一次异常信息
 * @author sloth-boot
 * @since 1.0.0
 */
public record MessageFailureLogEvent(String topic, String tag, String msgId, String msgKey,
                                     String body, String consumerGroup, int retryTimes,
                                     String errorMessage) {
}
