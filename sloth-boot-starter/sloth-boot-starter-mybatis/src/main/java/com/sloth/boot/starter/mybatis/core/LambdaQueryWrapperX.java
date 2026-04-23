package com.sloth.boot.starter.mybatis.core;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

import java.util.Collection;

/**
 * LambdaQueryWrapper 扩展类。
 *
 * @param <T> 实体类型
 * @author sloth-boot
 * @since 1.0.0
 */
public class LambdaQueryWrapperX<T> extends LambdaQueryWrapper<T> {

    /**
     * 非空时执行 like 查询。
     *
     * @param column 字段
     * @param value  值
     * @return 当前包装器
     */
    public LambdaQueryWrapperX<T> likeIfPresent(SFunction<T, ?> column, Object value) {
        if (value != null && !String.valueOf(value).isBlank()) {
            like(column, value);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> eqIfPresent(SFunction<T, ?> column, Object value) {
        if (value != null && !String.valueOf(value).isBlank()) {
            eq(column, value);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> neIfPresent(SFunction<T, ?> column, Object value) {
        if (value != null && !String.valueOf(value).isBlank()) {
            ne(column, value);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> gtIfPresent(SFunction<T, ?> column, Object value) {
        if (value != null) {
            gt(column, value);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> geIfPresent(SFunction<T, ?> column, Object value) {
        if (value != null) {
            ge(column, value);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> ltIfPresent(SFunction<T, ?> column, Object value) {
        if (value != null) {
            lt(column, value);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> leIfPresent(SFunction<T, ?> column, Object value) {
        if (value != null) {
            le(column, value);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> betweenIfPresent(SFunction<T, ?> column, Object value1, Object value2) {
        if (value1 != null && value2 != null) {
            between(column, value1, value2);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> inIfPresent(SFunction<T, ?> column, Collection<?> values) {
        if (values != null && !values.isEmpty()) {
            in(column, values);
        }
        return this;
    }

    @SafeVarargs
    public final LambdaQueryWrapperX<T> orderByDesc(SFunction<T, ?>... columns) {
        super.orderByDesc(columns);
        return this;
    }
}
