package com.sloth.boot.starter.mq.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;

/**
 * 消息消费失败日志默认监听器。
 * <p>
 * 默认将失败日志记录到日志文件，业务方可覆盖此 Bean 实现持久化到数据库。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
public class MessageFailureLogListener {

    @EventListener
    public void onMessageFailure(MessageFailureLogEvent event) {
        log.error("[MQ] 消息消费失败: topic={}, tag={}, msgId={}, group={}, retryTimes={}, error={}",
            event.getTopic(), event.getTag(), event.getMsgId(),
            event.getConsumerGroup(), event.getRetryTimes(), event.getErrorMessage());
    }
}
