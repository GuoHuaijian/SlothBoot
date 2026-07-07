package com.sloth.boot.example.observability.infrastructure.repository.mapper;

import com.sloth.boot.example.observability.infrastructure.model.po.product.DemoProduct;
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
public interface DemoProductMapper extends BaseMapperX<DemoProduct> {

    /**
     * 查询全部商品并按 id 升序。
     *
     * @return 商品列表
     */
    List<DemoProduct> selectAllOrdered();
}
