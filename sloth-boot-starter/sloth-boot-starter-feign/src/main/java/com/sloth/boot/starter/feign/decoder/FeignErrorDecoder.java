package com.sloth.boot.starter.feign.decoder;

import com.sloth.boot.starter.feign.exception.RateLimitException;
import com.sloth.boot.starter.feign.exception.RemoteCallException;
import com.sloth.boot.starter.feign.exception.ServiceNotFoundException;
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
        if (status == 404) {
            return new ServiceNotFoundException("远程服务不存在: " + methodKey);
        }
        if (status == 429) {
            return new RateLimitException("远程服务限流: " + methodKey);
        }
        if (status >= 500) {
            return new RemoteCallException(status, "远程服务调用失败: " + methodKey);
        }
        return new RemoteCallException(status, "远程调用异常: " + methodKey);
    }
}
