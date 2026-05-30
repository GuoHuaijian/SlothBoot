package com.sloth.boot.starter.rocketmq.event;

import com.sloth.boot.common.event.BaseEvent;
import lombok.Getter;

/**
 * 消息消费失败日志事件。
 * <p>
 * 当消息消费超过最大重试次数后发布，业务方可监听此事件实现失败日志持久化。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
public class MessageFailureLogEvent extends BaseEvent {

    /**
     * 消息 Topic。
     */
    private final String topic;

    /**
     * 消息 Tag。
     */
    private final String tag;

    /**
     * 消息 ID。
     */
    private final String msgId;

    /**
     * 消息 Key。
     */
    private final String msgKey;

    /**
     * 消息体。
     */
    private final String body;

    /**
     * 消费组。
     */
    private final String consumerGroup;

    /**
     * 已重试次数。
     */
    private final int retryTimes;

    /**
     * 最后一次异常信息。
     */
    private final String errorMessage;

    public MessageFailureLogEvent(Object source, String topic, String tag, String msgId,
                                  String msgKey, String body, String consumerGroup,
                                  int retryTimes, String errorMessage) {
        super(source);
        this.topic = topic;
        this.tag = tag;
        this.msgId = msgId;
        this.msgKey = msgKey;
        this.body = body;
        this.consumerGroup = consumerGroup;
        this.retryTimes = retryTimes;
        this.errorMessage = errorMessage;
    }
}
