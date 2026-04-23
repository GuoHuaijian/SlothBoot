package com.sloth.boot.common.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring 上下文工具类
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

    /**
     * 获取 ApplicationContext
     *
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 获取 Bean
     *
     * @param name Bean 名称
     * @return Bean 实例
     */
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    /**
     * 获取 Bean
     *
     * @param name Bean 名称
     * @param clazz Bean 类型
     * @param <T>  Bean 类型
     * @return Bean 实例
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

    /**
     * 获取 Bean
     *
     * @param clazz Bean 类型
     * @param <T>  Bean 类型
     * @return Bean 实例
     */
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    /**
     * 发布事件
     *
     * @param event 事件
     */
    public static void publishEvent(Object event) {
        getApplicationContext().publishEvent(event);
    }

    /**
     * 获取当前激活的 Profile
     *
     * @return Profile 名称
     */
    public static String getActiveProfile() {
        return getApplicationContext().getEnvironment().getActiveProfiles()[0];
    }

    /**
     * 获取配置属性
     *
     * @param key 配置键
     * @return 配置值
     */
    public static String getProperty(String key) {
        return getApplicationContext().getEnvironment().getProperty(key);
    }
}
