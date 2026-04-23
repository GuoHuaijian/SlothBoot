package com.sloth.boot.starter.mq.model;

import com.sloth.boot.common.context.TraceContext;
import com.sloth.boot.common.util.IdUtil;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息基类。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
public class BaseMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息唯一 ID。
     */
    private String msgId = IdUtil.nanoId();

    /**
     * 链路追踪 ID。
     */
    private String traceId = TraceContext.getTraceId();

    /**
     * 业务唯一键。
     */
    private String bizKey;

    /**
     * 消息创建时间。
     */
    private LocalDateTime createTime = LocalDateTime.now();

    /**
     * 扩展头。
     */
    private Map<String, String> headers = new HashMap<>();
}
