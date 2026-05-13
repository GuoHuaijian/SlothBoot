package com.sloth.boot.starter.mybatis.interceptor;

import com.sloth.boot.common.annotation.DataScope;
import com.sloth.boot.common.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Properties;

/**
 * 数据权限拦截器。
 * <p>
 * 根据 {@link DataScope} 注解和 {@link UserContext#getDataScope()} 自动追加 WHERE 条件。
 * <p>
 * 支持的数据范围类型：
 * <ul>
 *   <li>{@code all} - 全部数据（不追加条件）</li>
 *   <li>{@code dept} - 本部门数据</li>
 *   <li>{@code dept_and_below} - 本部门及以下数据</li>
 *   <li>{@code self} - 仅本人数据</li>
 * </ul>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class DataScopeInterceptor implements Interceptor {

    private static final String SCOPE_ALL = "all";
    private static final String SCOPE_DEPT = "dept";
    private static final String SCOPE_DEPT_AND_BELOW = "dept_and_below";
    private static final String SCOPE_SELF = "self";

    /**
     * 拦截 SQL 执行，根据 {@link DataScope} 注解和用户上下文自动追加数据权限 WHERE 条件。
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
        if (dataScope == null) {
            return invocation.proceed();
        }

        String dataScopeType = UserContext.getDataScope();
        if (dataScopeType == null || SCOPE_ALL.equals(dataScopeType)) {
            return invocation.proceed();
        }

        String sql = boundSql.getSql();
        String scopedSql = buildScopedSql(sql, dataScope, dataScopeType);
        if (scopedSql != null) {
            metaObject.setValue("delegate.boundSql.sql", scopedSql);
        }
        return invocation.proceed();
    }

    private String buildScopedSql(String sql, DataScope dataScope, String dataScopeType) {
        String condition = buildCondition(dataScope, dataScopeType);
        if (condition == null) {
            return null;
        }
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            if (statement instanceof Select select) {
                PlainSelect plainSelect = select.getPlainSelect();
                if (plainSelect != null) {
                    if (plainSelect.getWhere() != null) {
                        plainSelect.setWhere(new AndExpression(plainSelect.getWhere(),
                            CCJSqlParserUtil.parseCondExpression(condition)));
                    } else {
                        plainSelect.setWhere(CCJSqlParserUtil.parseCondExpression(condition));
                    }
                    return select.toString();
                }
            }
        } catch (Exception e) {
            log.warn("DataScope SQL 解析失败, 降级为字符串拼接, sql={}", sql, e);
        }
        // 降级：简单字符串拼接
        if (sql.toLowerCase().contains(" where ")) {
            return sql + " AND " + condition;
        }
        return sql + " WHERE " + condition;
    }

    private String buildCondition(DataScope dataScope, String dataScopeType) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return null;
        }

        String safeUserId = String.valueOf(userId);
        return switch (dataScopeType) {
            case SCOPE_SELF -> {
                String userAlias = dataScope.userAlias();
                if (userAlias.isEmpty()) {
                    yield "create_by = " + safeUserId;
                }
                yield userAlias + ".create_by = " + safeUserId;
            }
            case SCOPE_DEPT -> {
                String deptAlias = dataScope.deptAlias();
                String userAlias = dataScope.userAlias();
                if (deptAlias.isEmpty()) {
                    yield null;
                }
                yield userAlias.isEmpty()
                    ? "dept_id IN (SELECT dept_id FROM sys_user WHERE user_id = " + safeUserId + ")"
                    : deptAlias + ".dept_id = " + userAlias + ".dept_id AND " + userAlias + ".user_id = " + safeUserId;
            }
            case SCOPE_DEPT_AND_BELOW -> {
                String deptAlias = dataScope.deptAlias();
                if (deptAlias.isEmpty()) {
                    yield null;
                }
                yield deptAlias + ".dept_id IN (SELECT dept_id FROM sys_dept WHERE dept_id = "
                    + "(SELECT dept_id FROM sys_user WHERE user_id = " + safeUserId + ") "
                    + "OR find_in_set(dept_id, (SELECT ancestors FROM sys_dept WHERE dept_id = "
                    + "(SELECT dept_id FROM sys_user WHERE user_id = " + safeUserId + "))))";
            }
            default -> null;
        };
    }

    /**
     * 包装目标对象生成代理。
     *
     * @param target 目标对象
     * @return 代理对象
     */
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    /**
     * 设置插件属性。
     *
     * @param properties 属性
     */
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
