package com.sloth.boot.example.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.sloth.boot.example.domain.entity.SysDept;
import com.sloth.boot.starter.mybatis.annotation.DataScope;
import com.sloth.boot.starter.mybatis.core.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 部门 Mapper
 * <p>
 * 演示能力：
 * <ul>
 *   <li>{@link BaseMapperX} — 扩展分页查询、按字段查询</li>
 *   <li>{@code insertBatch} — 由 InsertBatchSqlInjector 自动注入的单语句批量插入</li>
 *   <li>{@link DataScope} — 传统数据权限，根据 UserContext.getDataScope() 自动追加 WHERE 条件</li>
 * </ul>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Mapper
public interface SysDeptMapper extends BaseMapperX<SysDept> {

    /**
     * 批量插入部门（单语句，由 InsertBatchSqlInjector 注入）
     *
     * @param list 部门列表
     * @return 插入行数
     */
    int insertBatch(@Param("list") List<SysDept> list);

    /**
     * 带数据权限的部门列表查询
     * <p>
     * 当前用户的 dataScope 为 "dept" 时，仅返回本部门数据；
     * 为 "self" 时，仅返回自己创建的数据；为 "all" 时返回全部。
     *
     * @param wrapper 查询条件
     * @return 部门列表
     */
    @DataScope(deptAlias = "d")
    List<SysDept> selectListWithScope(@Param("ew") Wrapper<SysDept> wrapper);
}
