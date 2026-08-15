package com.sloth.boot.common.util;

import com.sloth.boot.common.context.TraceContext;
import com.sloth.boot.common.context.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ContextSnapshot 上下文快照测试")
class ContextSnapshotTest {

    @AfterEach
    void cleanup() {
        UserContext.clear();
        TraceContext.clear();
        MDC.clear();
    }

    @Test
    @DisplayName("capture 捕获当前线程的 Trace/User/MDC")
    void capture_snapshotsCurrentContext() {
        UserContext.set(buildUserInfo());
        TraceContext.set(buildTraceInfo());
        MDC.put("mdcKey", "mdcValue");

        ContextSnapshot snapshot = ContextSnapshot.capture();

        snapshot.apply();
        assertThat(UserContext.getUsername()).isEqualTo("admin");
        assertThat(TraceContext.getTraceId()).isEqualTo("abc123");
        assertThat(MDC.get("mdcKey")).isEqualTo("mdcValue");
    }

    @Test
    @DisplayName("decorate(Runnable) 执行前应用上下文，执行后清除")
    void decorateRunnable_appliesThenClears() {
        UserContext.set(buildUserInfo());
        TraceContext.set(buildTraceInfo());
        MDC.put("mdcKey", "mdcValue");

        AtomicReference<String> seenUsername = new AtomicReference<>();
        ContextSnapshot snapshot = ContextSnapshot.capture();

        Runnable decorated = snapshot.decorate(() -> {
            seenUsername.set(UserContext.getUsername());
            assertThat(TraceContext.getTraceId()).isEqualTo("abc123");
            assertThat(MDC.get("mdcKey")).isEqualTo("mdcValue");
        });

        UserContext.clear();
        TraceContext.clear();
        MDC.clear();
        decorated.run();

        assertThat(seenUsername).hasValue("admin");
        assertThat(UserContext.get()).isNull();
        assertThat(TraceContext.get()).isNull();
        assertThat(MDC.get("mdcKey")).isNull();
    }

    @Test
    @DisplayName("decorate(Callable) 返回调用结果并清除上下文")
    void decorateCallable_returnsValueAndClears() {
        UserContext.set(buildUserInfo());
        ContextSnapshot snapshot = ContextSnapshot.capture();
        UserContext.clear();

        String result;
        try {
            result = snapshot.decorate((java.util.concurrent.Callable<String>) () -> UserContext.getUsername()).call();
        } catch (Exception e) {
            throw new AssertionError(e);
        }

        assertThat(result).isEqualTo("admin");
        assertThat(UserContext.get()).isNull();
    }

    @Test
    @DisplayName("decorate(Runnable) 发生异常时仍清除上下文")
    void decorateRunnable_clearsOnException() {
        UserContext.set(buildUserInfo());
        ContextSnapshot snapshot = ContextSnapshot.capture();
        UserContext.clear();

        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                snapshot.decorate((Runnable) () -> {
                    throw new IllegalStateException("boom");
                }).run())
            .isInstanceOf(IllegalStateException.class);

        assertThat(UserContext.get()).isNull();
    }

    private static UserContext.UserInfo buildUserInfo() {
        UserContext.UserInfo info = new UserContext.UserInfo();
        info.setUserId(100L);
        info.setUsername("admin");
        info.setRoles(Set.of("ROLE_ADMIN"));
        return info;
    }

    private static TraceContext.TraceInfo buildTraceInfo() {
        TraceContext.TraceInfo info = new TraceContext.TraceInfo();
        info.setTraceId("abc123");
        info.setSpanId("span001");
        return info;
    }
}
