package com.sloth.boot.example.application.model.convert.product;

import com.sloth.boot.example.application.model.form.product.ProductCreateForm;
import com.sloth.boot.example.application.model.vo.product.ProductVO;
import com.sloth.boot.example.infrastructure.model.po.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * 商品对象转换器（MapStruct）。
 * <p>
 * 负责商品相关对象之间的转换，包括：
 * - 表单对象 → 实体对象
 * - 实体对象 → 视图对象
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductConvert {

    /**
     * 将商品创建表单转换为实体对象。
     *
     * @param form 商品创建表单
     * @return 商品实体
     */
    Product toEntity(ProductCreateForm form);

    /**
     * 将商品实体转换为视图对象。
     *
     * @param entity 商品实体
     * @return 商品视图对象
     */
    ProductVO toVO(Product entity);

    /**
     * 将商品实体列表转换为视图对象列表。
     *
     * @param entities 商品实体列表
     * @return 商品视图对象列表
     */
    List<ProductVO> toVOList(List<Product> entities);
}
