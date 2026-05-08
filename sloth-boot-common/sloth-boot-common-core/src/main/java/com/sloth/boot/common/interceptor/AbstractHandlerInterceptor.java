package com.sloth.boot.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 通用 Handler 拦截器基类
 * <p>
 * 提供模板方法，子类只需重写感兴趣的方法。默认实现不做任何操作。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public abstract class AbstractHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           org.springframework.web.servlet.ModelAndView modelAndView) {
        // 子类可重写
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 子类可重写
    }
}
