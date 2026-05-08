package com.sloth.boot.starter.feign.decoder;

import com.sloth.boot.common.exception.RemoteCallException;
import com.sloth.boot.common.exception.RemoteRateLimitException;
import com.sloth.boot.common.exception.RemoteServiceNotFoundException;

import feign.Response;
import feign.codec.ErrorDecoder;

/**
 * Feign 异常解码器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class FeignErrorDecoder implements ErrorDecoder {

    /**
     * 解码 HTTP 非 2xx 异常。
     *
     * @param methodKey 方法标识
     * @param response  响应
     * @return 异常对象
     */
    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();
        String serviceName = extractServiceName(methodKey);
        if (status == 404) {
            return new RemoteServiceNotFoundException(serviceName);
        }
        if (status == 429) {
            return new RemoteRateLimitException(serviceName);
        }
        if (status >= 500) {
            return new RemoteCallException(serviceName, status, "远程服务调用失败: " + methodKey);
        }
        return new RemoteCallException(serviceName, status, "远程调用异常: " + methodKey);
    }

    /**
     * 从方法标识中提取服务名称（格式：ServiceName#methodName）。
     *
     * @param methodKey 方法标识
     * @return 服务名称
     */
    private String extractServiceName(String methodKey) {
        int idx = methodKey.indexOf('#');
        return idx > 0 ? methodKey.substring(0, idx) : methodKey;
    }
}
