package com.sloth.boot.example.observability.application.model.convert;

import com.sloth.boot.example.observability.application.model.vo.UserVO;
import com.sloth.boot.example.observability.infrastructure.model.po.user.DemoUser;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * 用户对象转换器（MapStruct）。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserConvert {

    UserVO toVO(DemoUser entity);

    List<UserVO> toVOList(List<DemoUser> entities);
}
