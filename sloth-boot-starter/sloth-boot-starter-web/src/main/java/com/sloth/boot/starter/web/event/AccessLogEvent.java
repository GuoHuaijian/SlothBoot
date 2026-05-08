package com.sloth.boot.starter.web.event;

import com.sloth.boot.common.event.BaseEvent;
import lombok.Getter;

/**
 * API 访问日志事件。
 * <p>
 * 每次 HTTP 请求完成后发布，业务方可监听此事件实现访问日志持久化。
 * <pre>
 * &#64;EventListener
 * public void onAccess(AccessLogEvent event) {
 *     accessLogService.save(event);
 * }
 * </pre>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
public class AccessLogEvent extends BaseEvent {

    /**
     * 请求方法（GET/POST/PUT/DELETE）。
     */
    private final String method;

    /**
     * 请求 URI。
     */
    private final String uri;

    /**
     * 查询参数。
     */
    private final String queryString;

    /**
     * 客户端 IP。
     */
    private final String clientIp;

    /**
     * User-Agent。
     */
    private final String userAgent;

    /**
     * 用户 ID。
     */
    private final Long userId;

    /**
     * HTTP 响应状态码。
     */
    private final Integer statusCode;

    /**
     * 请求执行耗时（毫秒）。
     */
    private final long elapsed;

    /**
     * 请求体内容（可选，仅当 bodyCache 开启时有值）。
     */
    private final String requestBody;

    public AccessLogEvent(Object source, String method, String uri, String queryString,
                           String clientIp, String userAgent, Long userId,
                           Integer statusCode, long elapsed, String requestBody) {
        super(source);
        this.method = method;
        this.uri = uri;
        this.queryString = queryString;
        this.clientIp = clientIp;
        this.userAgent = userAgent;
        this.userId = userId;
        this.statusCode = statusCode;
        this.elapsed = elapsed;
        this.requestBody = requestBody;
    }
}
