package com.sloth.boot.example.infrastructure.repository.mapper.product;

import com.sloth.boot.example.infrastructure.model.po.product.Product;
import com.sloth.boot.starter.mybatis.core.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品数据访问层。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Mapper
public interface ProductMapper extends BaseMapperX<Product> {

    List<Product> listProduct();
}
