package com.sloth.boot.starter.sms.model;

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
public class SendResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否成功。
     */
    private boolean success;

    /**
     * 消息 ID。
     */
    private String msgId;

    /**
     * 结果消息。
     */
    private String message;
}
