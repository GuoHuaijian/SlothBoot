package com.sloth.boot.starter.mybatis.core;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

import java.util.Arrays;
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

    /**
     * 非空时执行 eq 查询。
     *
     * @param column 字段
     * @param value  值
     * @return 当前包装器
     */
    public LambdaQueryWrapperX<T> eqIfPresent(SFunction<T, ?> column, Object value) {
        if (value != null && !String.valueOf(value).isBlank()) {
            eq(column, value);
        }
        return this;
    }

    /**
     * 非空时执行 ne 查询。
     *
     * @param column 字段
     * @param value  值
     * @return 当前包装器
     */
    public LambdaQueryWrapperX<T> neIfPresent(SFunction<T, ?> column, Object value) {
        if (value != null && !String.valueOf(value).isBlank()) {
            ne(column, value);
        }
        return this;
    }

    /**
     * 非空时执行 gt 查询。
     *
     * @param column 字段
     * @param value  值
     * @return 当前包装器
     */
    public LambdaQueryWrapperX<T> gtIfPresent(SFunction<T, ?> column, Object value) {
        if (value != null) {
            gt(column, value);
        }
        return this;
    }

    /**
     * 非空时执行 ge 查询。
     *
     * @param column 字段
     * @param value  值
     * @return 当前包装器
     */
    public LambdaQueryWrapperX<T> geIfPresent(SFunction<T, ?> column, Object value) {
        if (value != null) {
            ge(column, value);
        }
        return this;
    }

    /**
     * 非空时执行 lt 查询。
     *
     * @param column 字段
     * @param value  值
     * @return 当前包装器
     */
    public LambdaQueryWrapperX<T> ltIfPresent(SFunction<T, ?> column, Object value) {
        if (value != null) {
            lt(column, value);
        }
        return this;
    }

    /**
     * 非空时执行 le 查询。
     *
     * @param column 字段
     * @param value  值
     * @return 当前包装器
     */
    public LambdaQueryWrapperX<T> leIfPresent(SFunction<T, ?> column, Object value) {
        if (value != null) {
            le(column, value);
        }
        return this;
    }

    /**
     * 两端非空时执行 between 查询。
     *
     * @param column 字段
     * @param value1 起始值
     * @param value2 结束值
     * @return 当前包装器
     */
    public LambdaQueryWrapperX<T> betweenIfPresent(SFunction<T, ?> column, Object value1, Object value2) {
        if (value1 != null && value2 != null) {
            between(column, value1, value2);
        }
        return this;
    }

    /**
     * 集合非空时执行 in 查询。
     *
     * @param column 字段
     * @param values 值集合
     * @return 当前包装器
     */
    public LambdaQueryWrapperX<T> inIfPresent(SFunction<T, ?> column, Collection<?> values) {
        if (values != null && !values.isEmpty()) {
            in(column, values);
        }
        return this;
    }

    /**
     * 按多字段降序排序。
     *
     * @param columns 排序字段
     * @return 当前包装器
     */
    @SafeVarargs
    public final LambdaQueryWrapperX<T> orderByDesc(SFunction<T, ?>... columns) {
        if (columns != null && columns.length > 0) {
            super.orderByDesc(Arrays.asList(columns));
        }
        return this;
    }
}
