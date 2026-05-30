package com.sloth.boot.example.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sloth.boot.example.domain.entity.SysUser;
import com.sloth.boot.starter.mybatis.annotation.DataPermission;
import com.sloth.boot.starter.mybatis.core.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户 Mapper
 * <p>
 * 演示能力：
 * <ul>
 *   <li>{@link BaseMapperX} — 分页查询 selectPage(BaseQuery, Wrapper)</li>
 *   <li>{@code insertBatch} — 单语句批量插入</li>
 *   <li>{@link DataPermission} — 增强型数据权限（支持 SpEL 表达式）</li>
 * </ul>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Mapper
public interface SysUserMapper extends BaseMapperX<SysUser> {

    /**
     * 批量插入用户（单语句，由 InsertBatchSqlInjector 注入）
     *
     * @param list 用户列表
     * @return 插入行数
     */
    int insertBatch(@Param("list") List<SysUser> list);

    /**
     * 带数据权限的分页查询
     * <p>
     * 通过 @DataPermission 注解，根据当前用户的数据权限范围自动追加 WHERE 条件。
     * 支持的传统范围：all（全部）、dept（本部门）、dept_and_below（本部门及下级）、self（仅本人）。
     *
     * @param page    分页参数
     * @param wrapper 查询条件
     * @return 分页结果
     */
    @DataPermission(deptAlias = "u", userAlias = "u")
    Page<SysUser> selectPageWithPermission(Page<SysUser> page, @Param("ew") Wrapper<SysUser> wrapper);
}
