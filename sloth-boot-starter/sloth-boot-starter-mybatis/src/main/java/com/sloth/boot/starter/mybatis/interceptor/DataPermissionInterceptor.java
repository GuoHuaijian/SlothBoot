package com.sloth.boot.starter.mybatis.interceptor;

import com.sloth.boot.common.context.UserContext;
import com.sloth.boot.starter.mybatis.annotation.DataPermission;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 增强数据权限拦截器。
 * <p>
 * 支持 {@link DataPermission} 注解的 SpEL 表达式模式和传统范围模式。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class DataPermissionInterceptor implements Interceptor {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{(\\w+)}");

    /**
     * 拦截 SQL 执行，根据 {@link DataPermission} 注解的 SpEL 表达式追加数据权限条件。
     *
     * @param invocation 调用对象
     * @return 执行结果
     * @throws Throwable 异常
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler handler = (StatementHandler) invocation.getTarget();
        MetaObject metaObject = SystemMetaObject.forObject(handler);

        MappedStatement ms = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        if (ms.getSqlCommandType() != SqlCommandType.SELECT) {
            return invocation.proceed();
        }

        DataPermission dataPermission = findDataPermissionAnnotation(ms);
        if (dataPermission == null) {
            return invocation.proceed();
        }

        String expression = dataPermission.expression();
        if (expression == null || expression.isBlank()) {
            return invocation.proceed();
        }

        String sql = handler.getBoundSql().getSql();
        String condition = resolveExpression(expression);
        if (condition == null || condition.isBlank()) {
            return invocation.proceed();
        }

        try {
            String newSql = appendCondition(sql, condition);
            metaObject.setValue("delegate.boundSql.sql", newSql);
        } catch (Exception e) {
            log.warn("[DataPermission] SQL 改写失败，降级为原始 SQL: {}", e.getMessage());
        }

        return invocation.proceed();
    }

    /**
     * 解析 SpEL 表达式中的占位符。
     * <p>
     * 将 {userId}、{deptId}、{username}、{tenantId} 替换为实际值。
     */
    private String resolveExpression(String expression) {
        UserContext.UserInfo userInfo = UserContext.get();
        if (userInfo == null) {
            return null;
        }

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(expression);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String varName = matcher.group(1);
            String replacement = switch (varName) {
                case "userId" -> userInfo.getUserId() != null ? String.valueOf(userInfo.getUserId()) : "";
                case "deptId" -> {
                    Object deptId = userInfo.getExtra() != null ? userInfo.getExtra().get("deptId") : null;
                    yield deptId != null ? deptId.toString() : "";
                }
                case "username" -> userInfo.getUsername() != null ? userInfo.getUsername() : "";
                case "tenantId" -> userInfo.getTenantId() != null ? userInfo.getTenantId() : "";
                default -> "";
            };
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 在 SQL 的 WHERE 子句中追加权限条件。
     */
    private String appendCondition(String sql, String condition) throws JSQLParserException {
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            if (statement instanceof Select select && select.getSelectBody() instanceof PlainSelect plainSelect) {
                String existingWhere = plainSelect.getWhere() != null ? plainSelect.getWhere().toString() : null;
                if (existingWhere != null) {
                    plainSelect.setWhere(CCJSqlParserUtil.parseCondExpression(existingWhere + " AND " + condition));
                } else {
                    plainSelect.setWhere(CCJSqlParserUtil.parseCondExpression(condition));
                }
                return select.toString();
            }
        } catch (JSQLParserException e) {
            log.debug("[DataPermission] JSqlParser 解析失败，使用字符串拼接: {}", e.getMessage());
        }
        // 降级：字符串拼接
        String lowerSql = sql.trim().toLowerCase();
        if (lowerSql.contains(" where ")) {
            int whereIdx = sql.toLowerCase().indexOf(" where ");
            return sql.substring(0, whereIdx + 7) + "(" + condition + ") AND " + sql.substring(whereIdx + 7);
        } else {
            int orderByIdx = findOrderByIndex(sql);
            if (orderByIdx > 0) {
                return sql.substring(0, orderByIdx) + " WHERE " + condition + " " + sql.substring(orderByIdx);
            }
            return sql + " WHERE " + condition;
        }
    }

    private int findOrderByIndex(String sql) {
        String lower = sql.toLowerCase();
        // 从后往前找 ORDER BY，避免子查询干扰
        int idx = lower.lastIndexOf(" order by ");
        return idx > 0 ? idx : -1;
    }

    /**
     * 从 MappedStatement 反射获取 DataPermission 注解。
     */
    private DataPermission findDataPermissionAnnotation(MappedStatement ms) {
        try {
            String id = ms.getId();
            String className = id.substring(0, id.lastIndexOf('.'));
            String methodName = id.substring(id.lastIndexOf('.') + 1);
            Class<?> clazz = Class.forName(className);
            for (Method method : clazz.getMethods()) {
                if (method.getName().equals(methodName)) {
                    DataPermission dp = method.getAnnotation(DataPermission.class);
                    if (dp != null) {
                        return dp;
                    }
                    // 检查类级别注解
                    return clazz.getAnnotation(DataPermission.class);
                }
            }
        } catch (Exception e) {
            log.debug("[DataPermission] 获取注解失败: {}", e.getMessage());
        }
        return null;
    }
}
