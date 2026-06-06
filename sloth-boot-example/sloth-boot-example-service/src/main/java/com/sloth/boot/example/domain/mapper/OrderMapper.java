package com.sloth.boot.example.domain.mapper;

import com.sloth.boot.example.domain.entity.DemoOrder;
import com.sloth.boot.starter.mybatis.core.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单 Mapper 接口
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Mapper
public interface OrderMapper extends BaseMapperX<DemoOrder> {
}
