package com.sloth.boot.starter.auth.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.listener.SaTokenEventCenter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.sloth.boot.starter.auth.filter.TokenRenewalFilter;
import com.sloth.boot.starter.auth.handler.SaTokenContextHandler;
import com.sloth.boot.starter.auth.listener.SaTokenEventListener;
import com.sloth.boot.starter.auth.properties.AuthProperties;
import com.sloth.boot.starter.auth.service.OnlineUserService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 认证授权自动配置。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(name = "cn.dev33.satoken.stp.StpUtil")
@ConditionalOnProperty(prefix = "sloth.auth", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(AuthProperties.class)
@ComponentScan(basePackages = "com.sloth.boot.starter.auth.handler")
public class AuthAutoConfiguration {

    /**
     * 注册 Sa-Token 上下文处理器。
     *
     * @return 上下文处理器
     */
    @Bean
    @ConditionalOnMissingBean
    public SaTokenContextHandler saTokenContextHandler() {
        return new SaTokenContextHandler();
    }

    /**
     * 注册认证拦截器配置。
     *
     * @param authProperties       认证配置
     * @param saTokenContextHandler 上下文处理器
     * @return WebMvc 配置
     */
    @Bean
    @ConditionalOnMissingBean(name = "authWebMvcConfigurer")
    public WebMvcConfigurer authWebMvcConfigurer(AuthProperties authProperties,
                                                  SaTokenContextHandler saTokenContextHandler) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new SaInterceptor(handle -> {
                    // 白名单路径放行
                    SaRouter.match(authProperties.getWhiteList()).stop();

                    // 黑名单路径拦截
                    SaRouter.match(authProperties.getBlackList()).match(r -> {
                        throw new cn.dev33.satoken.exception.NotPermissionException("黑名单禁止访问");
                    });

                    // 其他路径校验登录
                    SaRouter.match("/**").check(r -> {
                        StpUtil.checkLogin();
                        // 登录成功后同步到 UserContext
                        saTokenContextHandler.syncToUserContext();
                    });
                })).addPathPatterns("/**")
                  .excludePathPatterns(authProperties.getWhiteList());
            }
        };
    }

    // ==================== 新增特性 ====================

    /**
     * 注册 Sa-Token 事件监听桥接器（登录/登出事件发布为 Spring Event）。
     *
     * @param eventPublisher Spring 事件发布器
     * @param authProperties 认证配置
     * @return Sa-Token 事件监听器
     */
    @Bean
    @ConditionalOnMissingBean
    public SaTokenEventListener saTokenEventListener(ApplicationEventPublisher eventPublisher,
                                                      AuthProperties authProperties) {
        SaTokenEventListener listener = new SaTokenEventListener(eventPublisher, authProperties);
        SaTokenEventCenter.registerListener(listener);
        return listener;
    }

    /**
     * 注册在线用户管理服务。
     *
     * @return 在线用户服务
     */
    @Bean
    @ConditionalOnMissingBean
    public OnlineUserService onlineUserService() {
        return new OnlineUserService();
    }

    /**
     * 注册 Token 续期过滤器（滑动过期）。
     *
     * @param authProperties 认证配置
     * @return 过滤器注册 Bean
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "sloth.auth", name = "active-timeout", havingValue = "-1", matchIfMissing = false)
    public FilterRegistrationBean<TokenRenewalFilter> tokenRenewalFilterRegistration(AuthProperties authProperties) {
        FilterRegistrationBean<TokenRenewalFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new TokenRenewalFilter(authProperties));
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.LOWEST_PRECEDENCE - 10);
        return registration;
    }
}
