package com.sloth.boot.starter.mybatis.interceptor;

import com.sloth.boot.common.annotation.DataScope;
import com.sloth.boot.common.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Properties;

/**
 * 数据权限拦截器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class DataScopeInterceptor implements Interceptor {

    /**
     * 执行数据权限拦截。
     *
     * @param invocation 调用对象
     * @return 执行结果
     * @throws Throwable 异常
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        String mappedStatementId = (String) metaObject.getValue("delegate.mappedStatement.id");
        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
        DataScope dataScope = resolveDataScope(mappedStatementId);
        if (dataScope != null && UserContext.getDataScope() != null) {
            String sql = boundSql.getSql();
            String scopedSql = sql + " /* data_scope:" + UserContext.getDataScope()
                    + ", deptAlias=" + dataScope.deptAlias()
                    + ", userAlias=" + dataScope.userAlias() + " */";
            metaObject.setValue("delegate.boundSql.sql", scopedSql);
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // no-op
    }

    private DataScope resolveDataScope(String mappedStatementId) {
        try {
            int lastDot = mappedStatementId.lastIndexOf('.');
            Class<?> mapperClass = Class.forName(mappedStatementId.substring(0, lastDot));
            String methodName = mappedStatementId.substring(lastDot + 1);
            for (Method method : mapperClass.getMethods()) {
                if (method.getName().equals(methodName) && method.isAnnotationPresent(DataScope.class)) {
                    return method.getAnnotation(DataScope.class);
                }
            }
        } catch (ClassNotFoundException ex) {
            log.debug("解析 DataScope 失败, mappedStatementId={}", mappedStatementId, ex);
        }
        return null;
    }
}
