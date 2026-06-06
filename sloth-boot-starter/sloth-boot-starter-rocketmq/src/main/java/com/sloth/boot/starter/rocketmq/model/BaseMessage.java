package com.sloth.boot.starter.rocketmq.model;

import com.sloth.boot.common.context.TraceContext;
import com.sloth.boot.common.util.IdUtil;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "消息基类")
public class BaseMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息唯一 ID。
     */
    @Schema(description = "消息唯一ID")
    private String msgId = IdUtil.nanoId();

    /**
     * 链路追踪 ID。
     */
    @Schema(description = "链路追踪ID")
    private String traceId = TraceContext.getTraceId();

    /**
     * 业务唯一键。
     */
    @Schema(description = "业务唯一键")
    private String bizKey;

    /**
     * 消息创建时间。
     */
    @Schema(description = "消息创建时间")
    private LocalDateTime createTime = LocalDateTime.now();

    /**
     * 扩展头。
     */
    @Schema(description = "扩展头信息")
    private Map<String, String> headers = new HashMap<>(4);
}
