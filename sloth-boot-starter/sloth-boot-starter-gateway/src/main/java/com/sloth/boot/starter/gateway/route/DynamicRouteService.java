package com.sloth.boot.starter.gateway.route;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import tools.jackson.core.type.TypeReference;
import com.sloth.boot.common.util.JsonUtil;
import com.sloth.boot.starter.gateway.config.GatewayProperties;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.core.env.Environment;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * 动态路由服务。
 * <p>
 * 监听 Nacos 配置变更，动态刷新 Gateway 路由。
 * 自动清理不再存在的旧路由，避免路由残留。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
public class DynamicRouteService implements DisposableBean {

    private final RouteDefinitionWriter routeDefinitionWriter;
    private final GatewayProperties gatewayProperties;
    private final Environment environment;
    private ConfigService configService;
    private final Set<String> loadedRouteIds = new HashSet<>();

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
            this.configService = NacosFactory.createConfigService(properties);
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
            log.error("[Gateway] 初始化动态路由失败", ex);
        }
    }

    private void refreshRoutes(String configInfo) {
        try {
            List<RouteDefinition> routes = JsonUtil.parseObject(configInfo, new TypeReference<List<RouteDefinition>>() {
            });
            if (routes == null) {
                return;
            }
            Set<String> newRouteIds = new HashSet<>();
            for (RouteDefinition route : routes) {
                newRouteIds.add(route.getId());
                routeDefinitionWriter.save(Mono.just(route)).subscribe();
            }
            for (String oldId : loadedRouteIds) {
                if (!newRouteIds.contains(oldId)) {
                    routeDefinitionWriter.delete(Mono.just(oldId)).subscribe();
                    log.info("[Gateway] 删除过期路由: id={}", oldId);
                }
            }
            loadedRouteIds.clear();
            loadedRouteIds.addAll(newRouteIds);
            log.info("[Gateway] 动态路由刷新完成, size={}", routes.size());
        } catch (Exception ex) {
            log.error("[Gateway] 刷新动态路由失败", ex);
        }
    }

    @Override
    public void destroy() {
        if (configService != null) {
            try {
                configService.shutDown();
            } catch (Exception e) {
                log.warn("[Gateway] ConfigService 关闭异常", e);
            }
        }
    }
}
