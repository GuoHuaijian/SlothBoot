package com.sloth.boot.common.context;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("上下文类测试")
class ContextTest {

    @AfterEach
    void cleanup() {
        UserContext.clear();
        TraceContext.clear();
    }

    @Nested
    @DisplayName("UserContext")
    class UserContextTest {

        @Test
        @DisplayName("set/get 正确存取用户信息")
        void setAndGet() {
            UserContext.UserInfo info = new UserContext.UserInfo();
            info.setUserId(100L);
            info.setUsername("admin");
            info.setTenantId("t1");
            info.setRoles(Set.of("ROLE_ADMIN", "ROLE_USER"));

            UserContext.set(info);

            assertThat(UserContext.getUserId()).isEqualTo(100L);
            assertThat(UserContext.getUsername()).isEqualTo("admin");
            assertThat(UserContext.getTenantId()).isEqualTo("t1");
            assertThat(UserContext.getRoles()).containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
        }

        @Test
        @DisplayName("未设置时 get 返回 null")
        void getReturnsNullWhenNotSet() {
            assertThat(UserContext.get()).isNull();
            assertThat(UserContext.getUserId()).isNull();
            assertThat(UserContext.getUsername()).isNull();
        }

        @Test
        @DisplayName("clear 清除用户信息")
        void clearRemovesContext() {
            UserContext.UserInfo info = new UserContext.UserInfo();
            info.setUserId(1L);
            UserContext.set(info);

            UserContext.clear();

            assertThat(UserContext.get()).isNull();
        }

        @Test
        @DisplayName("getRoles 未设置时返回空集合")
        void getRolesReturnsEmptySetWhenNotSet() {
            assertThat(UserContext.getRoles()).isEmpty();
        }

        @Test
        @DisplayName("getDataScope 和 getExtra 正确返回")
        void dataScopeAndExtra() {
            UserContext.UserInfo info = new UserContext.UserInfo();
            info.setDataScope("dept");
            info.setExtra(Map.of("key", "value"));
            UserContext.set(info);

            assertThat(UserContext.getDataScope()).isEqualTo("dept");
            assertThat(UserContext.getExtra()).containsEntry("key", "value");
        }
    }

    @Nested
    @DisplayName("TraceContext")
    class TraceContextTest {

        @Test
        @DisplayName("set/get 正确存取追踪信息")
        void setAndGet() {
            TraceContext.TraceInfo info = new TraceContext.TraceInfo();
            info.setTraceId("abc123");
            info.setSpanId("span001");

            TraceContext.set(info);

            assertThat(TraceContext.getTraceId()).isEqualTo("abc123");
            assertThat(TraceContext.getSpanId()).isEqualTo("span001");
        }

        @Test
        @DisplayName("未设置时返回 null")
        void returnsNullWhenNotSet() {
            assertThat(TraceContext.get()).isNull();
            assertThat(TraceContext.getTraceId()).isNull();
            assertThat(TraceContext.getSpanId()).isNull();
        }

        @Test
        @DisplayName("generateTraceId 生成 16 位十六进制字符串")
        void generateTraceId() {
            String traceId = TraceContext.generateTraceId();
            assertThat(traceId).hasSize(16).matches("[a-f0-9]+");
        }

        @Test
        @DisplayName("generateTraceId 每次生成不同值")
        void generateTraceIdIsUnique() {
            String id1 = TraceContext.generateTraceId();
            String id2 = TraceContext.generateTraceId();
            assertThat(id1).isNotEqualTo(id2);
        }

        @Test
        @DisplayName("clear 清除追踪信息")
        void clearRemovesContext() {
            TraceContext.TraceInfo info = new TraceContext.TraceInfo();
            info.setTraceId("abc");
            TraceContext.set(info);

            TraceContext.clear();

            assertThat(TraceContext.get()).isNull();
        }
    }
}
