package com.sloth.boot.common.result;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PageResult 测试")
class PageResultTest {

    @Test
    @DisplayName("of 工厂方法正确计算分页信息")
    void ofCalculatesCorrectly() {
        PageResult<String> result = PageResult.of(List.of("a", "b", "c"), 100, 1, 10);

        assertThat(result.getList()).containsExactly("a", "b", "c");
        assertThat(result.getTotal()).isEqualTo(100);
        assertThat(result.getPageNum()).isEqualTo(1);
        assertThat(result.getPageSize()).isEqualTo(10);
        assertThat(result.getTotalPages()).isEqualTo(10);
    }

    @Test
    @DisplayName("totalPages 向上取整")
    void totalPagesRoundsUp() {
        PageResult<String> result = PageResult.of(List.of(), 11, 1, 10);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("空结果 totalPages 为 0")
    void emptyResultTotalPages() {
        PageResult<String> result = PageResult.of(List.of(), 0, 1, 10);
        assertThat(result.getTotalPages()).isZero();
    }

    @Test
    @DisplayName("isSuccess 始终返回 true")
    void isSuccessAlwaysTrue() {
        PageResult<String> result = PageResult.of(List.of(), 0, 1, 10);
        assertThat(result.isSuccess()).isTrue();
    }
}
