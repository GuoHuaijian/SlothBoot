package com.sloth.boot.starter.mybatis.core;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sloth.boot.common.base.BaseQuery;
import com.sloth.boot.common.result.PageResult;

import java.util.Collection;

/**
 * 扩展 Mapper 基类。
 *
 * @param <T> 实体类型
 * @author sloth-boot
 * @since 1.0.0
 */
public interface BaseMapperX<T> extends BaseMapper<T> {

    /**
     * 执行分页查询。
     *
     * @param query   查询对象
     * @param wrapper 条件构造器
     * @return 分页结果
     */
    default PageResult<T> selectPage(BaseQuery query, Wrapper<T> wrapper) {
        BaseQuery actualQuery = query == null ? new BaseQuery() : query;
        Page<T> page = new Page<>(actualQuery.getPageNum(), actualQuery.getPageSize());
        Page<T> result = this.selectPage(page, wrapper);
        return toPageResult(result);
    }

    /**
     * 按字段查询单条记录。
     *
     * @param field 字段名
     * @param value 字段值
     * @return 查询结果
     */
    default T selectOne(String field, Object value) {
        LambdaQueryWrapperX<T> wrapper = new LambdaQueryWrapperX<>();
        wrapper.apply(field + " = {0}", value).last("LIMIT 1");
        return this.selectOne(wrapper);
    }

    /**
     * 逐条插入（适用于小数据量场景，如 10 条以内）。
     * <p>
     * 注意：此方法不是真批量，而是循环逐条 INSERT。
     * 大数据量请使用 {@code InsertBatchSomeColumn} 注入器实现真批量。
     *
     * @param list 实体集合
     * @return 影响行数
     */
    default int insertOneByOne(Collection<T> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        int rows = 0;
        for (T entity : list) {
            rows += this.insert(entity);
        }
        return rows;
    }

    /**
     * 将 MyBatis-Plus 分页对象转换为 PageResult
     *
     * @param page MyBatis-Plus 分页对象
     * @param <E>  数据类型
     * @return PageResult
     */
    static <E> PageResult<E> toPageResult(Page<E> page) {
        return PageResult.of(page.getRecords(), page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }
}
