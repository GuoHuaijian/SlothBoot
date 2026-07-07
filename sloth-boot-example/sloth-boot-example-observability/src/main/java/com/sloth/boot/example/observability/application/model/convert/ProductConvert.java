package com.sloth.boot.example.observability.application.model.convert;

import com.sloth.boot.example.observability.application.model.vo.ProductVO;
import com.sloth.boot.example.observability.infrastructure.model.po.product.DemoProduct;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * 商品对象转换器（MapStruct）。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductConvert {

    ProductVO toVO(DemoProduct entity);

    List<ProductVO> toVOList(List<DemoProduct> entities);
}
