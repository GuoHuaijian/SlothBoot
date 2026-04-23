package com.sloth.boot.common.log.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志DTO
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
public class OperateLogDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 追踪ID
     */
    private String traceId;

    /**
     * 模块
     */
    private String module;

    /**
     * 描述
     */
    private String description;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 方法名
     */
    private String method;

    /**
     * 请求URL
     */
    private String requestUrl;

    /**
     * 请求方法
     */
    private String requestMethod;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 响应结果
     */
    private String responseResult;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 操作人ID
     */
    private String operatorId;

    /**
     * 操作人名称
     */
    private String operatorName;

    /**
     * 操作人IP
     */
    private String operatorIp;

    /**
     * 操作人位置
     */
    private String operatorLocation;

    /**
     * User-Agent
     */
    private String userAgent;

    /**
     * 耗时（毫秒）
     */
    private Long costTime;

    /**
     * 状态（0成功 1失败）
     */
    private Integer status;

    /**
     * 操作时间
     */
    private LocalDateTime operateTime;
}