package com.sloth.boot.example.application.model.convert.user;

import com.sloth.boot.example.application.model.form.user.UserCreateForm;
import com.sloth.boot.example.application.model.form.user.UserUpdateForm;
import com.sloth.boot.example.application.model.query.user.UserPageQry;
import com.sloth.boot.example.application.model.vo.user.SysUserVO;
import com.sloth.boot.example.infrastructure.model.po.user.SysUser;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * 用户对象转换器（MapStruct）。
 * <p>
 * 负责用户相关对象之间的转换，包括：
 * - 表单对象 → 实体对象
 * - 实体对象 → 视图对象
 * - 查询条件 → 实体对象
 * - 更新表单 → 实体对象（部分更新）
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserConvert {

    /**
     * 将用户创建表单转换为实体对象。
     *
     * @param form 用户创建表单
     * @return 用户实体
     */
    SysUser toEntity(UserCreateForm form);

    List<SysUser> toEntityList(List<UserCreateForm> forms);

    /**
     * 将用户查询条件转换为实体对象。
     *
     * @param query 用户分页查询条件
     * @return 用户实体（用于条件查询）
     */
    SysUser toEntity(UserPageQry query);

    /**
     * 将用户实体转换为视图对象。
     *
     * @param entity 用户实体
     * @return 用户视图对象
     */
    SysUserVO toVO(SysUser entity);

    List<SysUserVO> toVOList(List<SysUser> entities);

    /**
     * 将用户更新表单的非空字段更新到实体对象。
     *
     * @param form   用户更新表单
     * @param entity 目标用户实体
     */
    void updateEntity(UserUpdateForm form, @MappingTarget SysUser entity);
}
