package com.sloth.boot.example.infrastructure.repository.mapper.order;

import com.sloth.boot.example.infrastructure.model.po.order.DemoOrder;
import com.sloth.boot.starter.mybatis.core.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 订单数据访问层。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Mapper
public interface OrderMapper extends BaseMapperX<DemoOrder> {

    List<DemoOrder> listOrder();
}
