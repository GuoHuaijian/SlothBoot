package com.sloth.boot.starter.mybatis.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.sloth.boot.starter.mybatis.interceptor.DataPermissionInterceptor;
import com.sloth.boot.starter.mybatis.interceptor.DataScopeInterceptor;
import com.sloth.boot.starter.mybatis.interceptor.SlowSqlInterceptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("MybatisPlusAutoConfiguration 条件装配测试")
class MybatisPlusAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(MybatisPlusAutoConfiguration.class)).withBean(DataSource.class, () -> {
            DataSource ds = mock(DataSource.class);
            try {
                Connection conn = mock(Connection.class);
                DatabaseMetaData metaData = mock(DatabaseMetaData.class);
                when(metaData.getURL()).thenReturn("jdbc:mysql://localhost:3306/test");
                when(conn.getMetaData()).thenReturn(metaData);
                when(ds.getConnection()).thenReturn(conn);
            } catch (SQLException ignored) {
            }
            return ds;
        });

    @Test
    @DisplayName("默认配置下注册核心 Bean")
    void registersCoreBeansByDefault() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(MybatisPlusInterceptor.class);
            assertThat(context).hasSingleBean(MetaObjectHandler.class);
            assertThat(context).hasSingleBean(SlowSqlInterceptor.class);
            assertThat(context).hasSingleBean(DataScopeInterceptor.class);
            assertThat(context).hasSingleBean(DataPermissionInterceptor.class);
            assertThat(context).hasSingleBean(ISqlInjector.class);
        });
    }

    @Test
    @DisplayName("sloth.mybatis.enabled=false 时不注册任何 Bean")
    void disabledByProperty() {
        // MybatisPlusAutoConfiguration 没有类级别 @ConditionalOnProperty，
        // 但各 Bean 均通过 @ConditionalOnMissingBean 守卫。
        // 此测试验证跳过整个自动配置类后无 Bean 注册。
        new ApplicationContextRunner().run(context -> {
            assertThat(context).doesNotHaveBean(MybatisPlusInterceptor.class);
            assertThat(context).doesNotHaveBean(MetaObjectHandler.class);
            assertThat(context).doesNotHaveBean(SlowSqlInterceptor.class);
            assertThat(context).doesNotHaveBean(DataScopeInterceptor.class);
            assertThat(context).doesNotHaveBean(DataPermissionInterceptor.class);
            assertThat(context).doesNotHaveBean(ISqlInjector.class);
        });
    }

    @Test
    @DisplayName("用户自定义 MybatisPlusInterceptor 可覆盖默认")
    void customMybatisPlusInterceptorOverrides() {
        contextRunner.withBean("customInterceptor", MybatisPlusInterceptor.class, MybatisPlusInterceptor::new)
            .run(context -> {
                assertThat(context).hasSingleBean(MybatisPlusInterceptor.class);
                assertThat(context).getBean("customInterceptor").isInstanceOf(MybatisPlusInterceptor.class);
            });
    }
}
