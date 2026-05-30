package com.sloth.boot.starter.seata.tracing;

import io.seata.core.context.RootContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Seata 事务追踪过滤器。
 * <p>
 * 将 Seata 全局事务 ID（xid）写入 SLF4J MDC，
 * 使日志中可看到当前事务的 xid，支持全链路事务追踪。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
public class SeataTracingFilter extends OncePerRequestFilter {

    private static final String SEATA_XID_KEY = "seataXid";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String xid = RootContext.getXID();
        if (xid != null) {
            MDC.put(SEATA_XID_KEY, xid);
        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(SEATA_XID_KEY);
        }
    }
}
