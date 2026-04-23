package com.sloth.boot.starter.sentinel.handler;

import com.alibaba.csp.sentinel.adapter.spring.webmvc_v6x.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.sloth.boot.common.util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.nio.charset.StandardCharsets;

/**
 * Sentinel Web Block 异常处理器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class SentinelBlockExceptionHandler implements BlockExceptionHandler {

    private final GlobalBlockHandler globalBlockHandler;

    /**
     * 构造函数。
     *
     * @param globalBlockHandler 全局 Block 处理器
     */
    public SentinelBlockExceptionHandler(GlobalBlockHandler globalBlockHandler) {
        this.globalBlockHandler = globalBlockHandler;
    }

    /**
     * 处理 Sentinel Block 异常。
     *
     * @param request   请求
     * @param response  响应
     * @param exception 异常
     * @throws Exception 响应异常
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BlockException exception) throws Exception {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JsonUtil.toJson(globalBlockHandler.handle(exception)));
    }
}
