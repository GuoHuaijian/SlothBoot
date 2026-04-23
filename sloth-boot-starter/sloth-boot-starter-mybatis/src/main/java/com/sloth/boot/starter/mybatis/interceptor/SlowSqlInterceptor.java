package com.sloth.boot.starter.mybatis.interceptor;

import com.sloth.boot.starter.mybatis.config.MybatisPlusProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Arrays;
import java.util.Properties;

/**
 * 慢 SQL 拦截器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {
                MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class
        }),
        @Signature(type = Executor.class, method = "update", args = {
                MappedStatement.class, Object.class
        })
})
public class SlowSqlInterceptor implements Interceptor {

    private final MybatisPlusProperties mybatisPlusProperties;

    /**
     * 执行拦截逻辑。
     *
     * @param invocation 调用对象
     * @return 执行结果
     * @throws Throwable 异常
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = invocation.proceed();
        long cost = System.currentTimeMillis() - startTime;
        if (cost >= mybatisPlusProperties.getSlowSqlThreshold()) {
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            Object parameter = invocation.getArgs().length > 1 ? invocation.getArgs()[1] : null;
            BoundSql boundSql = mappedStatement.getBoundSql(parameter);
            log.warn("检测到慢 SQL, id={}, cost={}ms, sql={}, parameter={}",
                    mappedStatement.getId(),
                    cost,
                    normalize(boundSql.getSql()),
                    parameter);
        }
        return result;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // no-op
    }

    private String normalize(String sql) {
        return sql == null ? "" : sql.replaceAll("\\s+", " ").trim();
    }
}
