package com.sloth.boot.common.log.event;

import com.sloth.boot.common.context.TraceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 慢操作事件测试。
 */
class SlowOperationEventTest {

    @AfterEach
    void cleanup() {
        TraceContext.clear();
    }

    @Test
    void should_create_event_with_required_fields() {
        SlowOperationEvent event = new SlowOperationEvent(this, "SQL",
            "SELECT * FROM user WHERE id = ?", 1500, 500);

        assertThat(event.getOperationType()).isEqualTo("SQL");
        assertThat(event.getDetail()).isEqualTo("SELECT * FROM user WHERE id = ?");
        assertThat(event.getCostTimeMs()).isEqualTo(1500);
        assertThat(event.getThresholdMs()).isEqualTo(500);
        assertThat(event.getContext()).isNull();
        assertThat(event.getSource()).isEqualTo(this.toString());
    }

    @Test
    void should_create_event_with_context() {
        SlowOperationEvent event = new SlowOperationEvent(this, "ES_QUERY",
            "{\"match_all\":{}}", 3000, 1000, "index=products");

        assertThat(event.getOperationType()).isEqualTo("ES_QUERY");
        assertThat(event.getContext()).isEqualTo("index=products");
    }

    @Test
    void should_have_trace_id_from_base_event() {
        TraceContext.TraceInfo traceInfo = new TraceContext.TraceInfo();
        traceInfo.setTraceId("test-trace-id");
        TraceContext.set(traceInfo);
        SlowOperationEvent event = new SlowOperationEvent(this, "HTTP",
            "/api/users", 2000, 1000);

        assertThat(event.getTraceId()).isEqualTo("test-trace-id");
        assertThat(event.getEventTime()).isNotNull();
    }
}
