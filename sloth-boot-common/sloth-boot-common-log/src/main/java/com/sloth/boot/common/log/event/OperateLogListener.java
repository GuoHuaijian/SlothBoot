package com.sloth.boot.common.log.event;

import com.sloth.boot.common.log.OperateLogHandler;
import com.sloth.boot.common.log.model.OperateLogDTO;
import com.sloth.boot.common.log.config.LogProperties;
import com.sloth.boot.common.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;

/**
 * 操作日志监听器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class OperateLogListener {

    private final LogProperties logProperties;
    private final OperateLogHandler operateLogHandler;

    /**
     * 处理操作日志事件。
     *
     * @param event 操作日志事件
     */
    @EventListener
    public void handleOperateLogEvent(OperateLogEvent event) {
        OperateLogDTO dto = event.operateLog();
        if (logProperties.isPrintOperateLog()) {
            log.info("[Operate] {}", JsonUtil.toJson(dto));
        }
        operateLogHandler.handle(dto);
    }
}
