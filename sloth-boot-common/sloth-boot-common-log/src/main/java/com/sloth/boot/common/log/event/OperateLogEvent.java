package com.sloth.boot.common.log.event;

import com.sloth.boot.common.event.BaseEvent;
import com.sloth.boot.common.log.model.OperateLogDTO;

/**
 * 操作日志事件。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class OperateLogEvent extends BaseEvent {

    private final OperateLogDTO operateLog;

    /**
     * 构造操作日志事件。
     *
     * @param operateLog 操作日志
     */
    public OperateLogEvent(OperateLogDTO operateLog) {
        super(operateLog);
        this.operateLog = operateLog;
    }

    /**
     * 获取操作日志。
     *
     * @return 操作日志
     */
    public OperateLogDTO getOperateLog() {
        return operateLog;
    }
}
