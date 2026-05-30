package com.sloth.boot.example.domain.mapper;

import com.sloth.boot.example.domain.entity.Product;
import com.sloth.boot.starter.mybatis.core.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapperX<Product> {
}
