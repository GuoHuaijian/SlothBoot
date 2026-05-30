package com.sloth.boot.starter.mybatis.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.*;
import com.sloth.boot.common.context.UserContext;
import com.sloth.boot.starter.mybatis.handler.AutoFillMetaObjectHandler;
import com.sloth.boot.starter.mybatis.injector.InsertBatchSqlInjector;
import com.sloth.boot.starter.mybatis.interceptor.DataScopeInterceptor;
import com.sloth.boot.starter.mybatis.interceptor.SlowSqlInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * MyBatis Plus 自动配置。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnClass(MybatisPlusInterceptor.class)
@EnableConfigurationProperties(MybatisPlusProperties.class)
@EnableTransactionManagement
public class MybatisPlusAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(MybatisPlusAutoConfiguration.class);

    /**
     * 注册 MyBatis Plus 主拦截器。
     *
     * @param properties  配置属性
     * @param environment 环境信息
     * @param dataSource  数据源，用于自动检测数据库类型
     * @return 主拦截器
     */
    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor(MybatisPlusProperties properties, Environment environment,
                                                         DataSource dataSource) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(detectDbType(dataSource)));
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
     * @param properties MyBatis Plus 配置属性
     * @return 自动填充处理器
     */
    @Bean
    @ConditionalOnMissingBean(MetaObjectHandler.class)
    public MetaObjectHandler metaObjectHandler(MybatisPlusProperties properties) {
        return new AutoFillMetaObjectHandler(properties);
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
     * 注册增强数据权限拦截器（支持 SpEL 表达式）。
     *
     * @return 增强数据权限拦截器
     */
    @Bean
    @ConditionalOnMissingBean(DataPermissionInterceptor.class)
    public DataPermissionInterceptor dataPermissionInterceptor() {
        return new DataPermissionInterceptor();
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

    /**
     * 注册 SqlSessionFactory。
     *
     * @param dataSource     数据源
     * @param interceptor    MyBatis Plus 拦截器
     * @return SqlSessionFactory
     * @throws Exception 异常
     */
    @Bean
    @ConditionalOnMissingBean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource,
                                               MybatisPlusInterceptor interceptor) throws Exception {
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setPlugins(interceptor);
        factory.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/**/*.xml"));
        factory.setGlobalConfig(new com.baomidou.mybatisplus.core.config.GlobalConfig());
        return factory.getObject();
    }

    /**
     * 注册 SqlSessionTemplate。
     *
     * @param sqlSessionFactory SqlSessionFactory
     * @return SqlSessionTemplate
     */
    @Bean
    @ConditionalOnMissingBean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    private boolean isDevProfile(Environment environment) {
        for (String profile : environment.getActiveProfiles()) {
            if ("dev".equalsIgnoreCase(profile)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据数据源 JDBC URL 自动检测数据库类型。
     *
     * @param dataSource 数据源
     * @return 数据库类型，默认返回 {@link DbType#MYSQL}
     */
    private DbType detectDbType(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            String url = connection.getMetaData().getURL();
            if (url == null) {
                return DbType.MYSQL;
            }
            if (url.startsWith("jdbc:mysql:")) {
                return DbType.MYSQL;
            } else if (url.startsWith("jdbc:postgresql:")) {
                return DbType.POSTGRE_SQL;
            } else if (url.startsWith("jdbc:oracle:")) {
                return DbType.ORACLE;
            } else if (url.startsWith("jdbc:sqlserver:")) {
                return DbType.SQL_SERVER;
            } else if (url.startsWith("jdbc:mariadb:")) {
                return DbType.MARIADB;
            } else if (url.startsWith("jdbc:h2:")) {
                return DbType.H2;
            } else if (url.startsWith("jdbc:sqlite:")) {
                return DbType.SQLITE;
            } else if (url.startsWith("jdbc:dm:")) {
                return DbType.DM;
            } else if (url.startsWith("jdbc:kingbase:")) {
                return DbType.KINGBASE_ES;
            } else if (url.startsWith("jdbc:oceanbase:")) {
                return DbType.OCEAN_BASE;
            }
            log.warn("[MyBatis] 无法识别数据库类型, JDBC URL: {}, 将使用默认 MySQL 方言", url);
            return DbType.MYSQL;
        } catch (SQLException e) {
            log.warn("[MyBatis] 检测数据库类型失败, 将使用默认 MySQL 方言", e);
            return DbType.MYSQL;
        }
    }
}
