package com.sloth.boot.starter.gateway.handler;

import com.sloth.boot.common.exception.GlobalErrorCode;
import com.sloth.boot.common.result.R;
import com.sloth.boot.common.util.I18nUtil;
import com.sloth.boot.common.util.JsonUtil;
import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Gateway 全局异常处理器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Order(-1)
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

    /**
     * 统一处理网关异常。
     *
     * @param exchange 请求上下文
     * @param ex       异常
     * @return Mono
     */
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String fallbackMsg = I18nUtil.getMessage(GlobalErrorCode.INTERNAL_ERROR.getI18nKey());
        String body = JsonUtil.toJson(R.fail(GlobalErrorCode.INTERNAL_ERROR.getCode(),
                ex.getMessage() != null ? ex.getMessage() : fallbackMsg));
        DataBufferFactory bufferFactory = response.bufferFactory();
        DataBuffer dataBuffer = bufferFactory.wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(dataBuffer));
    }
}
