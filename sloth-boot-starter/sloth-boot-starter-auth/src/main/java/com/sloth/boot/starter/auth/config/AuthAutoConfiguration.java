package com.sloth.boot.starter.auth.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.sloth.boot.starter.auth.handler.SaTokenContextHandler;
import com.sloth.boot.starter.auth.properties.AuthProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
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
}
