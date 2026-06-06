package com.sloth.boot.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

/**
 * 条件判断工具类
 * <p>
 * 提供常用的条件判断方法，用于 AOP 和拦截器中的条件评估。
 * 底层通过 {@link SpringContextUtil} 访问 Spring 容器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
public final class ConditionUtil {

    private ConditionUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static final String PROD_PROFILE = "prod";
    private static final String DEV_PROFILE = "dev";
    private static final String TEST_PROFILE = "test";

    /**
     * 是否生产环境
     *
     * @return 是否生产环境
     */
    public static boolean isProdProfile() {
        return hasActiveProfile(PROD_PROFILE);
    }

    /**
     * 是否开发环境
     *
     * @return 是否开发环境
     */
    public static boolean isDevProfile() {
        return hasActiveProfile(DEV_PROFILE);
    }

    /**
     * 是否测试环境
     *
     * @return 是否测试环境
     */
    public static boolean isTestProfile() {
        return hasActiveProfile(TEST_PROFILE);
    }

    /**
     * 是否存在指定配置
     *
     * @param key 配置键
     * @return 是否存在
     */
    public static boolean hasProperty(String key) {
        try {
            return SpringContextUtil.getProperty(key) != null;
        } catch (IllegalStateException | NoSuchBeanDefinitionException e) {
            log.trace("Failed to check property '{}'", key, e);
            return false;
        }
    }

    /**
     * 获取配置值（带默认值）
     *
     * @param key          配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public static String getProperty(String key, String defaultValue) {
        try {
            String value = SpringContextUtil.getProperty(key);
            return value != null ? value : defaultValue;
        } catch (IllegalStateException | NoSuchBeanDefinitionException e) {
            log.trace("Failed to get property '{}'", key, e);
            return defaultValue;
        }
    }

    /**
     * Spring 容器中是否存在指定 Bean
     *
     * @param beanClass Bean 类型
     * @return 是否存在
     */
    public static boolean isBeanPresent(Class<?> beanClass) {
        try {
            return SpringContextUtil.getApplicationContext().containsBean(beanClass.getName());
        } catch (IllegalStateException | NoSuchBeanDefinitionException e) {
            log.trace("Failed to check bean '{}'", beanClass.getSimpleName(), e);
            return false;
        }
    }

    /**
     * 是否包含指定的 active profile
     *
     * @param profile profile 名称
     * @return 是否包含
     */
    private static boolean hasActiveProfile(String profile) {
        try {
            String[] activeProfiles = SpringContextUtil.getApplicationContext().getEnvironment().getActiveProfiles();
            for (String activeProfile : activeProfiles) {
                if (profile.equalsIgnoreCase(activeProfile)) {
                    return true;
                }
            }
        } catch (IllegalStateException | NoSuchBeanDefinitionException e) {
            log.trace("Failed to check active profile '{}'", profile, e);
        }
        return false;
    }
}
