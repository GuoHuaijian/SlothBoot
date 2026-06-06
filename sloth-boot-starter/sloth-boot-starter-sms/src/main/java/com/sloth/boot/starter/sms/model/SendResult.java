package com.sloth.boot.starter.sms.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 短信发送结果。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Builder
@Schema(description = "短信发送结果")
public class SendResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否成功。
     */
    @Schema(description = "是否发送成功", example = "true")
    private boolean success;

    /**
     * 消息 ID。
     */
    @Schema(description = "消息ID", example = "sms-123456")
    private String msgId;

    /**
     * 结果消息。
     */
    @Schema(description = "结果消息", example = "发送成功")
    private String message;
}
