package com.sloth.boot.starter.web.event;

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
 * @param method      请求方法（GET/POST/PUT/DELETE）
 * @param uri         请求 URI
 * @param queryString 查询参数
 * @param clientIp    客户端 IP
 * @param userAgent   User-Agent
 * @param userId      用户 ID
 * @param statusCode  HTTP 响应状态码
 * @param elapsed     请求执行耗时（毫秒）
 * @param requestBody 请求体内容（可选，仅当 bodyCache 开启时有值）
 * @author sloth-boot
 * @since 1.0.0
 */
public record AccessLogEvent(String method, String uri, String queryString, String clientIp,
                             String userAgent, Long userId, Integer statusCode, long elapsed,
                             String requestBody) {
}
