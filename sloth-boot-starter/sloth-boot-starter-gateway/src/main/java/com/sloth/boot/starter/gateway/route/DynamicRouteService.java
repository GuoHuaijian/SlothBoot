package com.sloth.boot.starter.gateway.route;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.fasterxml.jackson.core.type.TypeReference;
import com.sloth.boot.common.util.JsonUtil;
import com.sloth.boot.starter.gateway.config.GatewayProperties;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.core.env.Environment;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * 动态路由服务。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
public class DynamicRouteService {

    private final RouteDefinitionWriter routeDefinitionWriter;
    private final GatewayProperties gatewayProperties;
    private final Environment environment;

    /**
     * 构造函数。
     *
     * @param routeDefinitionWriter RouteDefinitionWriter
     * @param gatewayProperties     Gateway 配置
     * @param environment           环境
     */
    public DynamicRouteService(RouteDefinitionWriter routeDefinitionWriter,
                               GatewayProperties gatewayProperties,
                               Environment environment) {
        this.routeDefinitionWriter = routeDefinitionWriter;
        this.gatewayProperties = gatewayProperties;
        this.environment = environment;
    }

    /**
     * 初始化动态路由监听。
     */
    @PostConstruct
    public void init() {
        if (!gatewayProperties.isDynamicRouteEnabled()) {
            return;
        }
        String serverAddr = environment.getProperty("spring.cloud.nacos.discovery.server-addr");
        if (serverAddr == null || serverAddr.isBlank()) {
            return;
        }
        try {
            Properties properties = new Properties();
            properties.setProperty("serverAddr", serverAddr);
            ConfigService configService = NacosFactory.createConfigService(properties);
            String dataId = environment.getProperty("spring.application.name", "gateway") + "-gateway-routes";
            configService.addListener(dataId, "DEFAULT_GROUP", new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String configInfo) {
                    refreshRoutes(configInfo);
                }
            });
            String config = configService.getConfig(dataId, "DEFAULT_GROUP", 3000);
            if (config != null && !config.isBlank()) {
                refreshRoutes(config);
            }
        } catch (Exception ex) {
            log.error("初始化动态路由失败", ex);
        }
    }

    private void refreshRoutes(String configInfo) {
        try {
            List<RouteDefinition> routes = JsonUtil.parseObject(configInfo, new TypeReference<List<RouteDefinition>>() {
            });
            if (routes == null) {
                return;
            }
            for (RouteDefinition route : routes) {
                routeDefinitionWriter.save(Mono.just(route)).subscribe();
            }
            log.info("动态路由刷新完成, size={}", routes.size());
        } catch (Exception ex) {
            log.error("刷新动态路由失败", ex);
        }
    }
}
