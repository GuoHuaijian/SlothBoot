package com.sloth.boot.example.domain.mapper;

import com.sloth.boot.example.domain.entity.DemoOrder;
import com.sloth.boot.starter.mybatis.core.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapperX<DemoOrder> {
}
