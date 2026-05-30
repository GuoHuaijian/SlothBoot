package com.sloth.boot.starter.seata.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;

/**
 * Seata 事务指标。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
public class SeataMetrics {

    private final Counter transactionTotal;
    private final Counter transactionCommit;
    private final Counter transactionRollback;
    private final Counter transactionTimeout;
    private final Timer transactionDuration;

    public SeataMetrics(MeterRegistry registry) {
        this.transactionTotal = Counter.builder("seata.transaction.total")
            .description("事务总数").register(registry);
        this.transactionCommit = Counter.builder("seata.transaction.commit")
            .description("事务提交次数").register(registry);
        this.transactionRollback = Counter.builder("seata.transaction.rollback")
            .description("事务回滚次数").register(registry);
        this.transactionTimeout = Counter.builder("seata.transaction.timeout")
            .description("事务超时次数").register(registry);
        this.transactionDuration = Timer.builder("seata.transaction.duration")
            .description("事务执行耗时").register(registry);
    }
}
