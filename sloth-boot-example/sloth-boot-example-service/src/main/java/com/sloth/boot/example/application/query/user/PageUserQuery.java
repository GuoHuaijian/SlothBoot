package com.sloth.boot.example.application.query.user;

import com.sloth.boot.common.result.PageResult;
import com.sloth.boot.example.application.model.convert.user.UserConvert;
import com.sloth.boot.example.application.model.query.user.UserPageQry;
import com.sloth.boot.example.application.model.vo.user.SysUserVO;
import com.sloth.boot.example.infrastructure.model.po.user.SysUser;
import com.sloth.boot.example.infrastructure.repository.mapper.user.SysUserMapper;
import com.sloth.boot.starter.mybatis.core.LambdaQueryWrapperX;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户分页查询。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class PageUserQuery {

    private final SysUserMapper sysUserMapper;
    private final UserConvert userConvert;

    /**
     * 执行用户分页查询。
     * <p>
     * 支持按用户名、手机号、部门ID、状态等条件进行模糊查询和精确查询。
     * 结果按创建时间倒序排列。
     *
     * @param query 分页查询条件
     * @return 分页查询结果
     */
    public PageResult<SysUserVO> execute(UserPageQry query) {
        LambdaQueryWrapperX<SysUser> wrapper = new LambdaQueryWrapperX<SysUser>()
            .likeIfPresent(SysUser::getUsername, query.getUsername())
            .likeIfPresent(SysUser::getPhone, query.getPhone())
            .eqIfPresent(SysUser::getDeptId, query.getDeptId())
            .eqIfPresent(SysUser::getStatus, query.getStatus());
        wrapper.orderByDesc(SysUser::getCreateTime);
        PageResult<SysUser> pageResult = sysUserMapper.selectPage(query, wrapper);
        List<SysUserVO> voList = userConvert.toVOList(pageResult.getList());
        return PageResult.of(voList, pageResult.getTotal(), pageResult.getPageNum(), pageResult.getPageSize());
    }
}
