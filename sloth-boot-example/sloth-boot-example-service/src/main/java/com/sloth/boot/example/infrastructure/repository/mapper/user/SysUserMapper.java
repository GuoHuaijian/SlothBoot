package com.sloth.boot.example.infrastructure.repository.mapper.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sloth.boot.example.infrastructure.model.po.user.SysUser;
import com.sloth.boot.starter.mybatis.annotation.DataPermission;
import com.sloth.boot.starter.mybatis.core.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户数据访问层。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Mapper
public interface SysUserMapper extends BaseMapperX<SysUser> {

    void insertBatch(@Param("list") List<SysUser> users);

    List<SysUser> listUser(SysUser condition);

    @DataPermission(deptAlias = "u", userAlias = "u")
    Page<SysUser> pageWithPermission(Page<SysUser> page, @Param("condition") SysUser condition);
}
