package com.sloth.boot.starter.sentinel.datasource;

import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.sloth.boot.common.util.JsonUtil;
import com.sloth.boot.starter.sentinel.config.SentinelProperties;
import jakarta.annotation.PostConstruct;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * Sentinel Nacos 数据源配置。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class NacosDataSourceConfig {

    private final SentinelProperties sentinelProperties;
    private final Environment environment;

    /**
     * 构造函数。
     *
     * @param sentinelProperties Sentinel 配置
     * @param environment        Spring 环境
     */
    public NacosDataSourceConfig(SentinelProperties sentinelProperties, Environment environment) {
        this.sentinelProperties = sentinelProperties;
        this.environment = environment;
    }

    /**
     * 初始化 Nacos 数据源。
     */
    @PostConstruct
    public void init() {
        String serverAddr = environment.getProperty("spring.cloud.nacos.config.server-addr",
                environment.getProperty("spring.cloud.nacos.discovery.server-addr"));
        String applicationName = environment.getProperty("spring.application.name", "application");
        if (serverAddr == null || serverAddr.isBlank()) {
            return;
        }
        registerFlowRules(serverAddr, applicationName);
        registerDegradeRules(serverAddr, applicationName);
        registerSystemRules(serverAddr, applicationName);
        registerParamFlowRules(serverAddr, applicationName);
    }

    private void registerFlowRules(String serverAddr, String applicationName) {
        ReadableDataSource<String, List<FlowRule>> dataSource = new NacosDataSource<>(
                serverAddr,
                sentinelProperties.getNacosGroupId(),
                applicationName + "-flow-rules",
                jsonConverter(new TypeReference<List<FlowRule>>() {
                })
        );
        FlowRuleManager.register2Property(dataSource.getProperty());
    }

    private void registerDegradeRules(String serverAddr, String applicationName) {
        ReadableDataSource<String, List<DegradeRule>> dataSource = new NacosDataSource<>(
                serverAddr,
                sentinelProperties.getNacosGroupId(),
                applicationName + "-degrade-rules",
                jsonConverter(new TypeReference<List<DegradeRule>>() {
                })
        );
        DegradeRuleManager.register2Property(dataSource.getProperty());
    }

    private void registerSystemRules(String serverAddr, String applicationName) {
        ReadableDataSource<String, List<SystemRule>> dataSource = new NacosDataSource<>(
                serverAddr,
                sentinelProperties.getNacosGroupId(),
                applicationName + "-system-rules",
                jsonConverter(new TypeReference<List<SystemRule>>() {
                })
        );
        SystemRuleManager.register2Property(dataSource.getProperty());
    }

    private void registerParamFlowRules(String serverAddr, String applicationName) {
        ReadableDataSource<String, List<ParamFlowRule>> dataSource = new NacosDataSource<>(
                serverAddr,
                sentinelProperties.getNacosGroupId(),
                applicationName + "-param-rules",
                jsonConverter(new TypeReference<List<ParamFlowRule>>() {
                })
        );
        ParamFlowRuleManager.register2Property(dataSource.getProperty());
    }

    private <T> Converter<String, T> jsonConverter(TypeReference<T> typeReference) {
        return source -> JsonUtil.parseObject(source, typeReference);
    }
}
