package com.sloth.boot.starter.mybatis.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.IllegalSQLInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.sloth.boot.common.context.UserContext;
import com.sloth.boot.starter.mybatis.handler.AutoFillMetaObjectHandler;
import com.sloth.boot.starter.mybatis.injector.InsertBatchSqlInjector;
import com.sloth.boot.starter.mybatis.interceptor.DataScopeInterceptor;
import com.sloth.boot.starter.mybatis.interceptor.SlowSqlInterceptor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * MyBatis Plus 自动配置。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfiguration
@EnableConfigurationProperties(MybatisPlusProperties.class)
public class MybatisPlusAutoConfiguration {

    /**
     * 注册 MyBatis Plus 主拦截器。
     *
     * @param properties  配置属性
     * @param environment 环境信息
     * @return 主拦截器
     */
    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor(MybatisPlusProperties properties, Environment environment) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        if (isDevProfile(environment)) {
            interceptor.addInnerInterceptor(new IllegalSQLInnerInterceptor());
        }
        if (properties.isTenantEnabled()) {
            interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {
                @Override
                public Expression getTenantId() {
                    String tenantId = UserContext.getTenantId();
                    return new StringValue(tenantId == null ? "" : tenantId);
                }

                @Override
                public String getTenantIdColumn() {
                    return properties.getTenantColumn();
                }

                @Override
                public boolean ignoreTable(String tableName) {
                    return properties.getTenantIgnoreTables().contains(tableName);
                }
            }));
        }
        return interceptor;
    }

    /**
     * 注册自动填充处理器。
     *
     * @return 自动填充处理器
     */
    @Bean
    @ConditionalOnMissingBean(MetaObjectHandler.class)
    public MetaObjectHandler metaObjectHandler() {
        return new AutoFillMetaObjectHandler();
    }

    /**
     * 注册慢 SQL 拦截器。
     *
     * @param properties 配置属性
     * @return 慢 SQL 拦截器
     */
    @Bean
    @ConditionalOnMissingBean(SlowSqlInterceptor.class)
    public SlowSqlInterceptor slowSqlInterceptor(MybatisPlusProperties properties) {
        return new SlowSqlInterceptor(properties);
    }

    /**
     * 注册数据权限拦截器。
     *
     * @return 数据权限拦截器
     */
    @Bean
    @ConditionalOnMissingBean(DataScopeInterceptor.class)
    public DataScopeInterceptor dataScopeInterceptor() {
        return new DataScopeInterceptor();
    }

    /**
     * 注册批量插入 SQL 注入器。
     *
     * @return SQL 注入器
     */
    @Bean
    @ConditionalOnMissingBean(ISqlInjector.class)
    public ISqlInjector sqlInjector() {
        return new InsertBatchSqlInjector();
    }

    private boolean isDevProfile(Environment environment) {
        for (String profile : environment.getActiveProfiles()) {
            if ("dev".equalsIgnoreCase(profile)) {
                return true;
            }
        }
        return environment.getActiveProfiles().length == 0;
    }
}
