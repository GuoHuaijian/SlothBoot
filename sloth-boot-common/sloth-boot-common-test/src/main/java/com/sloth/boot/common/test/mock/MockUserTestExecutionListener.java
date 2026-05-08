package com.sloth.boot.common.test.mock;

import com.sloth.boot.common.context.UserContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.util.Arrays;
import java.util.HashSet;

/**
 * MockUser 测试执行监听器。
 * <p>
 * 自动读取测试方法或测试类上的 {@link MockUser} 注解，在测试执行前填充 {@link UserContext}，
 * 测试执行后清除上下文，确保测试间互不干扰。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class MockUserTestExecutionListener extends AbstractTestExecutionListener {

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }

    @Override
    public void beforeTestMethod(TestContext testContext) {
        MockUser mockUser = findMockUser(testContext);
        if (mockUser == null) {
            return;
        }
        UserContext.UserInfo userInfo = new UserContext.UserInfo();
        userInfo.setUserId(mockUser.userId());
        userInfo.setUsername(mockUser.username());
        if (!mockUser.tenantId().isEmpty()) {
            userInfo.setTenantId(mockUser.tenantId());
        }
        if (mockUser.roles().length > 0) {
            userInfo.setRoles(new HashSet<>(Arrays.asList(mockUser.roles())));
        }
        if (!mockUser.dataScope().isEmpty()) {
            userInfo.setDataScope(mockUser.dataScope());
        }
        UserContext.set(userInfo);
    }

    @Override
    public void afterTestMethod(TestContext testContext) {
        UserContext.clear();
    }

    /**
     * 查找 {@link MockUser} 注解，优先从方法上查找，其次从类上查找。
     */
    private MockUser findMockUser(TestContext testContext) {
        MockUser mockUser = AnnotationUtils.findAnnotation(
            testContext.getTestMethod(), MockUser.class);
        if (mockUser == null) {
            mockUser = AnnotationUtils.findAnnotation(
                testContext.getTestClass(), MockUser.class);
        }
        return mockUser;
    }
}
