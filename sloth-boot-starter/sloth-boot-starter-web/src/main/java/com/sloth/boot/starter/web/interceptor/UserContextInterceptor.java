package com.sloth.boot.starter.web.interceptor;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.sloth.boot.common.constant.HeaderConstant;
import com.sloth.boot.common.context.UserContext;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 用户上下文拦截器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class UserContextInterceptor implements HandlerInterceptor {

    /**
     * 在请求进入控制器前写入用户上下文。
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param handler  处理器
     * @return 是否继续执行
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userIdHeader = request.getHeader(HeaderConstant.USER_ID);
        String username = request.getHeader(HeaderConstant.USERNAME);
        String tenantId = request.getHeader(HeaderConstant.TENANT_ID);
        if (StrUtil.isAllBlank(userIdHeader, username, tenantId)) {
            return true;
        }
        UserContext.UserInfo userInfo = new UserContext.UserInfo();
        if (NumberUtil.isLong(userIdHeader)) {
            userInfo.setUserId(Long.parseLong(userIdHeader));
        }
        userInfo.setUsername(username);
        userInfo.setTenantId(tenantId);
        UserContext.set(userInfo);
        return true;
    }

    /**
     * 请求完成后清理用户上下文。
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param handler  处理器
     * @param ex       异常对象
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
