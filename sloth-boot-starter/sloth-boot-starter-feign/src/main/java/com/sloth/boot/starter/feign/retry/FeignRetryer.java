package com.sloth.boot.starter.feign.retry;

import com.sloth.boot.starter.feign.config.FeignProperties;
import feign.RetryableException;
import feign.Retryer;
import lombok.extern.slf4j.Slf4j;

/**
 * Feign 可配置重试器。
 * <p>
 * 支持配置最大重试次数和退避间隔，仅对 5xx 和网络异常重试。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
public class FeignRetryer implements Retryer {

    private final int maxAttempts;
    private final long backoffMs;
    private int attempt;

    public FeignRetryer(FeignProperties properties) {
        this.maxAttempts = properties.getRetry().getMaxAttempts();
        this.backoffMs = properties.getRetry().getBackoffMs();
    }

    @Override
    public void continueOrPropagate(RetryableException e) {
        if (attempt >= maxAttempts) {
            throw e;
        }
        attempt++;
        long sleepTime = backoffMs * (long) Math.pow(2, attempt - 1);
        log.debug("[Feign] Retry attempt {}/{}, waiting {}ms", attempt, maxAttempts, sleepTime);
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw e;
        }
    }

    @Override
    public Retryer clone() {
        return new FeignRetryer(new FeignProperties());
    }
}
