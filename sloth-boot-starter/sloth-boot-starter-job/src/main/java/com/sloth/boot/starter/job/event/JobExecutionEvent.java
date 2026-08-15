package com.sloth.boot.starter.job.event;

/**
 * Job 执行事件。
 * <p>
 * 在定时任务执行完成时发布，用于监控和审计。
 *
 * @param handlerName 任务处理器名称
 * @param costTimeMs  执行耗时（毫秒）
 * @param success     是否执行成功
 * @param errorMessage 失败原因
 * @author sloth-boot
 * @since 1.0.0
 */
public record JobExecutionEvent(String handlerName, long costTimeMs,
                                boolean success, String errorMessage) {
}
