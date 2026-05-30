package com.sloth.boot.starter.seata.event;

import com.sloth.boot.common.event.BaseEvent;
import lombok.Getter;

/**
 * 全局事务事件。
 * <p>
 * 在分布式事务开始、提交、回滚时发布，用于审计和监控。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
public class GlobalTransactionEvent extends BaseEvent {

    public enum Status { BEGIN, COMMITTED, ROLLED_BACK, TIMEOUT }

    private final String xid;
    private final Status status;
    private final long costTimeMs;

    public GlobalTransactionEvent(Object source, String xid, Status status, long costTimeMs) {
        super(source);
        this.xid = xid;
        this.status = status;
        this.costTimeMs = costTimeMs;
    }
}
