package com.sloth.boot.common.base;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BaseQuery 测试")
class BaseQueryTest {

    @Test
    @DisplayName("默认值：pageNum=1, pageSize=10, orderDirection=desc")
    void defaultValues() {
        BaseQuery query = new BaseQuery();
        assertThat(query.getPageNum()).isEqualTo(1);
        assertThat(query.getPageSize()).isEqualTo(10);
        assertThat(query.getOrderDirection()).isEqualTo("desc");
        assertThat(query.getOrderBy()).isNull();
    }

    @Test
    @DisplayName("getOffset 第一页偏移量为 0")
    void offsetFirstPage() {
        BaseQuery query = new BaseQuery();
        assertThat(query.getOffset()).isZero();
    }

    @Test
    @DisplayName("getOffset 第二页偏移量正确")
    void offsetSecondPage() {
        BaseQuery query = new BaseQuery();
        query.setPageNum(2);
        query.setPageSize(20);
        assertThat(query.getOffset()).isEqualTo(20);
    }

    @Test
    @DisplayName("getOffset 第三页偏移量正确")
    void offsetThirdPage() {
        BaseQuery query = new BaseQuery();
        query.setPageNum(3);
        query.setPageSize(15);
        assertThat(query.getOffset()).isEqualTo(30);
    }

    @Test
    @DisplayName("自定义排序字段")
    void customOrderBy() {
        BaseQuery query = new BaseQuery();
        query.setOrderBy("createTime");
        query.setOrderDirection("asc");
        assertThat(query.getOrderBy()).isEqualTo("createTime");
        assertThat(query.getOrderDirection()).isEqualTo("asc");
    }
}
