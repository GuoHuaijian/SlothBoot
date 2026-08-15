package com.sloth.boot.starter.seata.event;

/**
 * 全局事务事件。
 * <p>
 * 在分布式事务开始、提交、回滚时发布，用于审计和监控。
 *
 * @param xid        全局事务 ID
 * @param status     事务状态
 * @param costTimeMs 事务耗时（毫秒）
 * @author sloth-boot
 * @since 1.0.0
 */
public record GlobalTransactionEvent(String xid, Status status, long costTimeMs) {

    /**
     * 全局事务状态。
     */
    public enum Status {
        /** 事务开始 */
        BEGIN,
        /** 事务已提交 */
        COMMITTED,
        /** 事务已回滚 */
        ROLLED_BACK,
        /** 事务超时 */
        TIMEOUT
    }
}
