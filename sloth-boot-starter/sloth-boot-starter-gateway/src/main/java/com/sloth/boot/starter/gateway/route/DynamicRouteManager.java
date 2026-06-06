package com.sloth.boot.starter.gateway.route;

/**
 * 动态路由管理能力接口。
 * <p>
 * 提供动态路由的初始化、销毁和刷新能力。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public interface DynamicRouteManager {

    /**
     * 初始化动态路由监听。
     */
    void init();

    /**
     * 销毁路由管理资源。
     */
    void destroy();

    /**
     * 根据 JSON 配置信息刷新路由。
     *
     * @param configInfo 路由定义 JSON 字符串
     */
    void refreshRoutes(String configInfo);
}
