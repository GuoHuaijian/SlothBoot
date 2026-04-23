package com.sloth.boot.common.log;

import com.sloth.boot.common.log.model.OperateLogDTO;

/**
 * 操作日志处理器接口
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@FunctionalInterface
public interface OperateLogHandler {

    /**
     * 处理操作日志
     *
     * @param dto 操作日志DTO
     */
    void handle(OperateLogDTO dto);
}