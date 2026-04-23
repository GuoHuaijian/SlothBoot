package com.sloth.boot.starter.monitor.config;

import com.sloth.boot.starter.monitor.alarm.AlarmService;
import com.sloth.boot.starter.monitor.alarm.DingTalkAlarmService;
import com.sloth.boot.starter.monitor.alarm.WeChatAlarmService;
import com.sloth.boot.starter.monitor.endpoint.InfoEndpoint;
import com.sloth.boot.starter.monitor.health.NacosHealthIndicator;
import com.sloth.boot.starter.monitor.health.RedisHealthIndicator;
import com.sloth.boot.starter.monitor.health.RocketMQHealthIndicator;
import com.sloth.boot.starter.monitor.metrics.HttpMetricsFilter;
import com.sloth.boot.starter.monitor.metrics.JvmMetricsConfig;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 监控自动配置。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfiguration
@EnableConfigurationProperties(MonitorProperties.class)
@ConditionalOnProperty(prefix = "sloth.monitor", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MonitorAutoConfiguration {

    /**
     * 注册 RestTemplate。
     *
     * @return RestTemplate
     */
    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * 注册告警服务。
     *
     * @param restTemplate      RestTemplate
     * @param monitorProperties 监控配置
     * @return 告警服务
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "sloth.monitor.alarm", name = "enabled", havingValue = "true")
    public AlarmService alarmService(RestTemplate restTemplate, MonitorProperties monitorProperties) {
        String type = monitorProperties.getAlarm().getType();
        if ("wechat".equalsIgnoreCase(type)) {
            return new WeChatAlarmService(restTemplate, monitorProperties);
        }
        return new DingTalkAlarmService(restTemplate, monitorProperties);
    }

    /**
     * 注册 Nacos 健康检查器。
     *
     * @param environment Spring 环境
     * @return 健康检查器
     */
    @Bean
    @ConditionalOnClass(name = "com.alibaba.cloud.nacos.NacosServiceManager")
    @ConditionalOnMissingBean(name = "nacosHealthIndicator")
    public NacosHealthIndicator nacosHealthIndicator(Environment environment) {
        return new NacosHealthIndicator(environment);
    }

    /**
     * 注册 Redis 健康检查器。
     *
     * @param redisConnectionFactory Redis 连接工厂
     * @return 健康检查器
     */
    @Bean
    @ConditionalOnClass(RedisConnectionFactory.class)
    @ConditionalOnMissingBean(name = "monitorRedisHealthIndicator")
    public RedisHealthIndicator monitorRedisHealthIndicator(RedisConnectionFactory redisConnectionFactory) {
        return new RedisHealthIndicator(redisConnectionFactory);
    }

    /**
     * 注册 RocketMQ 健康检查器。
     *
     * @param rocketMQTemplate RocketMQTemplate
     * @return 健康检查器
     */
    @Bean
    @ConditionalOnClass(RocketMQTemplate.class)
    @ConditionalOnMissingBean(name = "rocketMQHealthIndicator")
    public RocketMQHealthIndicator rocketMQHealthIndicator(RocketMQTemplate rocketMQTemplate) {
        return new RocketMQHealthIndicator(rocketMQTemplate);
    }

    /**
     * 注册 HTTP 指标过滤器。
     *
     * @param meterRegistry 指标注册中心
     * @param monitorProperties 配置
     * @param alarmServiceProvider 告警服务提供者
     * @return 过滤器注册器
     */
    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnClass(MeterRegistry.class)
    @ConditionalOnMissingBean
    public FilterRegistrationBean<HttpMetricsFilter> httpMetricsFilter(MeterRegistry meterRegistry,
                                                                       MonitorProperties monitorProperties,
                                                                       ObjectProvider<AlarmService> alarmServiceProvider) {
        FilterRegistrationBean<HttpMetricsFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new HttpMetricsFilter(meterRegistry, monitorProperties, alarmServiceProvider.getIfAvailable()));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(Ordered.LOWEST_PRECEDENCE - 10);
        return registrationBean;
    }

    /**
     * 注册 JVM 指标。
     *
     * @param meterRegistry 指标注册中心
     * @return JVM 指标配置
     */
    @Bean
    @ConditionalOnClass(MeterRegistry.class)
    @ConditionalOnMissingBean
    public JvmMetricsConfig jvmMetricsConfig(MeterRegistry meterRegistry) {
        return new JvmMetricsConfig(meterRegistry);
    }

    /**
     * 注册应用信息端点。
     *
     * @param environment Spring 环境
     * @return 信息端点
     */
    @Bean
    @ConditionalOnClass(Endpoint.class)
    @ConditionalOnMissingBean
    public InfoEndpoint appInfoEndpoint(Environment environment) {
        return new InfoEndpoint(environment);
    }

    /**
     * 自定义 Actuator 端点暴露配置。
     *
     * @return Bean 后处理器
     */
    @Bean
    @ConditionalOnClass(WebEndpointProperties.class)
    @ConditionalOnMissingBean(name = "monitorActuatorExposurePostProcessor")
    public BeanPostProcessor monitorActuatorExposurePostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof WebEndpointProperties webEndpointProperties) {
                    Collection<String> currentIncludes = webEndpointProperties.getExposure().getInclude();
                    Set<String> includes = new LinkedHashSet<>(currentIncludes == null ? Collections.emptySet() : currentIncludes);
                    includes.add("health");
                    includes.add("info");
                    includes.add("metrics");
                    includes.add("prometheus");
                    includes.add("threadPools");
                    includes.add("appInfo");
                    webEndpointProperties.getExposure().setInclude(includes);
                }
                return bean;
            }
        };
    }
}
