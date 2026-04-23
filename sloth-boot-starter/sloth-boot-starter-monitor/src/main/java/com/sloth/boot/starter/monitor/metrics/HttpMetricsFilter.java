package com.sloth.boot.starter.monitor.metrics;

import com.sloth.boot.starter.monitor.alarm.AlarmMessage;
import com.sloth.boot.starter.monitor.alarm.AlarmService;
import com.sloth.boot.starter.monitor.config.MonitorProperties;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

/**
 * HTTP 指标采集过滤器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class HttpMetricsFilter extends OncePerRequestFilter {

    private final MeterRegistry meterRegistry;
    private final MonitorProperties monitorProperties;
    private final AlarmService alarmService;

    /**
     * 执行 HTTP 指标采集。
     *
     * @param request     请求
     * @param response    响应
     * @param filterChain 过滤器链
     * @throws ServletException 异常
     * @throws IOException      异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long cost = System.currentTimeMillis() - startTime;
            Timer.builder("http.server.requests")
                    .tag("uri", request.getRequestURI())
                    .tag("method", request.getMethod())
                    .tag("status", String.valueOf(response.getStatus()))
                    .register(meterRegistry)
                    .record(Duration.ofMillis(cost));
            if (monitorProperties.isSlowApiEnabled() && cost >= monitorProperties.getSlowApiThreshold()) {
                log.warn("检测到慢接口, uri={}, method={}, status={}, cost={}ms",
                        request.getRequestURI(), request.getMethod(), response.getStatus(), cost);
                if (alarmService != null) {
                    AlarmMessage alarmMessage = new AlarmMessage();
                    alarmMessage.setTitle("慢接口告警");
                    alarmMessage.setContent("接口: " + request.getMethod() + " " + request.getRequestURI()
                            + "\n状态码: " + response.getStatus()
                            + "\n耗时: " + cost + "ms");
                    alarmService.send(alarmMessage);
                }
            }
        }
    }
}
