package com.sloth.boot.starter.feign.decoder;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sloth.boot.common.result.R;
import com.sloth.boot.common.util.JsonUtil;
import com.sloth.boot.starter.feign.exception.RemoteCallException;
import feign.Response;
import feign.Util;
import feign.codec.Decoder;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * Feign 响应解码器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class FeignResponseDecoder implements Decoder {

    private static final ObjectMapper OBJECT_MAPPER = JsonUtil.getObjectMapper().copy();

    private final Decoder delegate;

    /**
     * 构造函数。
     *
     * @param delegate 委托解码器
     */
    public FeignResponseDecoder(Decoder delegate) {
        this.delegate = delegate;
    }

    /**
     * 解码 Feign 响应。
     *
     * @param response 响应对象
     * @param type     目标类型
     * @return 解码结果
     * @throws IOException IO 异常
     */
    @Override
    public Object decode(Response response, Type type) throws IOException {
        if (response.body() == null) {
            return null;
        }
        String body = Util.toString(response.body().asReader(StandardCharsets.UTF_8));
        if (body.isBlank()) {
            return null;
        }
        if (!body.contains("\"code\"") || !body.contains("\"msg\"")) {
            return delegate.decode(rebuildResponse(response, body), type);
        }
        JavaType wrapperType = OBJECT_MAPPER.getTypeFactory()
                .constructParametricType(R.class, OBJECT_MAPPER.getTypeFactory().constructType(type));
        R<?> result = OBJECT_MAPPER.readValue(body, wrapperType);
        if (result.getCode() != 0) {
            throw new RemoteCallException(result.getCode(), result.getMsg());
        }
        if (isReturnR(type)) {
            return result;
        }
        Object data = result.getData();
        if (data == null) {
            return null;
        }
        return OBJECT_MAPPER.convertValue(data, OBJECT_MAPPER.getTypeFactory().constructType(type));
    }

    private boolean isReturnR(Type type) {
        if (type instanceof Class<?> clazz) {
            return R.class.isAssignableFrom(clazz);
        }
        if (type instanceof ParameterizedType parameterizedType) {
            return parameterizedType.getRawType() instanceof Class<?> raw && R.class.isAssignableFrom(raw);
        }
        return false;
    }

    private Response rebuildResponse(Response response, String body) {
        return response.toBuilder().body(body, StandardCharsets.UTF_8).build();
    }
}
