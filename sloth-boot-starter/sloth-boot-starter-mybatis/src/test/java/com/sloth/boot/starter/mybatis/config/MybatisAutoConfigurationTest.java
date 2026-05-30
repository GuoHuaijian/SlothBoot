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

@DisplayName("MybatisAutoConfiguration 条件装配测试")
class MybatisAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(MybatisAutoConfiguration.class)).withBean(DataSource.class, () -> {
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
    @DisplayName("默认配置下上下文可正常加载")
    void contextLoadsSuccessfully() {
        contextRunner.run(context -> {
            assertThat(context).hasNotFailed();
        });
    }

    @Test
    @DisplayName("空上下文不注册任何 Bean")
    void noBeansInEmptyContext() {
        new ApplicationContextRunner().run(context -> {
            assertThat(context).doesNotHaveBean(MybatisPlusInterceptor.class);
            assertThat(context).doesNotHaveBean(MetaObjectHandler.class);
            assertThat(context).doesNotHaveBean(SlowSqlInterceptor.class);
            assertThat(context).doesNotHaveBean(DataScopeInterceptor.class);
            assertThat(context).doesNotHaveBean(DataPermissionInterceptor.class);
            assertThat(context).doesNotHaveBean(ISqlInjector.class);
        });
    }
}
