package com.sloth.boot.example.application.query.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sloth.boot.common.result.PageResult;
import com.sloth.boot.example.application.model.convert.user.UserConvert;
import com.sloth.boot.example.application.model.query.user.UserPageQry;
import com.sloth.boot.example.application.model.vo.user.SysUserVO;
import com.sloth.boot.example.infrastructure.model.po.user.SysUser;
import com.sloth.boot.example.infrastructure.repository.mapper.user.SysUserMapper;
import com.sloth.boot.starter.mybatis.core.BaseMapperX;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户数据权限分页查询。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class PageUserPermissionQuery {

    private final SysUserMapper sysUserMapper;
    private final UserConvert userConvert;

    /**
     * 执行用户数据权限分页查询。
     * <p>
     * 根据当前用户的数据权限范围查询用户列表。
     * 不同角色可查看不同范围的数据（全部、本部门、仅本人）。
     *
     * @param query 分页查询条件
     * @return 分页查询结果
     */
    public PageResult<SysUserVO> execute(UserPageQry query) {
        SysUser condition = userConvert.toEntity(query);
        Page<SysUser> page = sysUserMapper.pageWithPermission(
            new Page<>(query.getPageNum(), query.getPageSize()), condition);
        PageResult<SysUser> pageResult = BaseMapperX.toPageResult(page);
        List<SysUserVO> voList = userConvert.toVOList(pageResult.getList());
        return PageResult.of(voList, pageResult.getTotal(), pageResult.getPageNum(), pageResult.getPageSize());
    }
}
