package com.sloth.boot.example.observability.infrastructure.repository.mapper;

import com.sloth.boot.example.observability.infrastructure.model.po.user.DemoUser;
import com.sloth.boot.starter.mybatis.core.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户数据访问层。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Mapper
public interface DemoUserMapper extends BaseMapperX<DemoUser> {

    /**
     * 查询全部用户并按 id 升序。
     *
     * @return 用户列表
     */
    List<DemoUser> selectAllOrdered();
}
