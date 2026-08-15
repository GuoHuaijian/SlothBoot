package com.sloth.boot.common.log.event;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 慢操作事件测试。
 */
class SlowOperationEventTest {

    @Test
    void should_create_event_with_required_fields() {
        SlowOperationEvent event = SlowOperationEvent.of("SQL",
            "SELECT * FROM user WHERE id = ?", 1500, 500);

        assertThat(event.operationType()).isEqualTo("SQL");
        assertThat(event.detail()).isEqualTo("SELECT * FROM user WHERE id = ?");
        assertThat(event.costTimeMs()).isEqualTo(1500);
        assertThat(event.thresholdMs()).isEqualTo(500);
        assertThat(event.context()).isNull();
    }

    @Test
    void should_create_event_with_context() {
        SlowOperationEvent event = new SlowOperationEvent("ES_QUERY",
            "{\"match_all\":{}}", 3000, 1000, "index=products");

        assertThat(event.operationType()).isEqualTo("ES_QUERY");
        assertThat(event.context()).isEqualTo("index=products");
    }
}
