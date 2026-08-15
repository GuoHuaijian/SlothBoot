package com.sloth.boot.common.log.event;

import com.sloth.boot.common.log.model.OperateLogDTO;

/**
 * 操作日志事件。
 * <p>
 * 普通 POJO 事件，通过 {@code ApplicationEventPublisher} 发布，业务方可用
 * {@code @EventListener} 监听。
 *
 * @param operateLog 操作日志
 * @author sloth-boot
 * @since 1.0.0
 */
public record OperateLogEvent(OperateLogDTO operateLog) {
}
