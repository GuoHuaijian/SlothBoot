package com.sloth.boot.example.observability.application.query;

import com.sloth.boot.common.exception.BizException;
import com.sloth.boot.example.observability.application.model.convert.UserConvert;
import com.sloth.boot.example.observability.application.model.enums.user.UserErrorCode;
import com.sloth.boot.example.observability.application.model.vo.UserVO;
import com.sloth.boot.example.observability.infrastructure.model.po.user.DemoUser;
import com.sloth.boot.example.observability.infrastructure.repository.mapper.DemoUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 用户详情查询（读操作）。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GetUserQuery {

    private final DemoUserMapper userMapper;
    private final UserConvert userConvert;

    /**
     * 执行用户详情查询。
     *
     * @param id 用户 ID
     * @return 用户视图对象
     */
    public UserVO execute(Long id) {
        log.info("Querying user: userId={}", id);
        DemoUser user = userMapper.selectById(id);
        if (user == null) {
            throw BizException.of(UserErrorCode.USER_NOT_FOUND, "用户不存在: " + id);
        }
        return userConvert.toVO(user);
    }
}
