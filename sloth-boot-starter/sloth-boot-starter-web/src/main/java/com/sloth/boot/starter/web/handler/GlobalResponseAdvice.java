package com.sloth.boot.starter.web.handler;

import com.sloth.boot.common.result.R;
import com.sloth.boot.common.util.JsonUtil;
import com.sloth.boot.starter.web.properties.SlothWebProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 全局响应包装处理器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalResponseAdvice implements ResponseBodyAdvice<Object> {

    private static final String SWAGGER_UI_URI = "/swagger-ui";
    private static final String API_DOCS_URI = "/v3/api-docs";
    private static final String KNIFE4J_URI = "/doc.html";

    private final SlothWebProperties slothWebProperties;

    /**
     * 判断是否需要执行响应包装。
     *
     * @param returnType    返回值类型
     * @param converterType 消息转换器类型
     * @return 是否支持
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        if (!slothWebProperties.isResponseWrapper()) {
            return false;
        }
        if (returnType.getContainingClass().isAnnotationPresent(SkipResponseWrapper.class)) {
            return false;
        }
        return !returnType.hasMethodAnnotation(SkipResponseWrapper.class);
    }

    /**
     * 包装控制器响应体。
     *
     * @param body                  原始响应体
     * @param returnType            返回值类型
     * @param selectedContentType   选中的内容类型
     * @param selectedConverterType 选中的消息转换器
     * @param request               请求对象
     * @param response              响应对象
     * @return 包装后的响应体
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (isSwaggerRequest(request) || body instanceof R<?>) {
            return body;
        }
        R<Object> result = R.ok(body);
        if (String.class.isAssignableFrom(returnType.getParameterType())) {
            return JsonUtil.toJson(result);
        }
        return result;
    }

    private boolean isSwaggerRequest(ServerHttpRequest request) {
        if (!(request instanceof ServletServerHttpRequest servletServerHttpRequest)) {
            return false;
        }
        String requestUri = servletServerHttpRequest.getServletRequest().getRequestURI();
        return requestUri.startsWith(SWAGGER_UI_URI)
                || requestUri.startsWith(API_DOCS_URI)
                || requestUri.startsWith(KNIFE4J_URI);
    }
}
