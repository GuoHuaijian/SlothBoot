package com.sloth.boot.example.domain.mapper;

import com.sloth.boot.example.domain.entity.Product;
import com.sloth.boot.starter.mybatis.core.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品 Mapper 接口
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Mapper
public interface ProductMapper extends BaseMapperX<Product> {
}
