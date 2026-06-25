package com.sloth.boot.example.application.query.product;

import com.sloth.boot.example.application.model.convert.product.ProductConvert;
import com.sloth.boot.example.application.model.vo.product.ProductVO;
import com.sloth.boot.example.infrastructure.repository.mapper.product.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 商品列表查询。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class ListProductsQuery {

    private final ProductMapper productMapper;
    private final ProductConvert productConvert;

    /**
     * 执行商品列表查询。
     *
     * @return 商品列表
     */
    public List<ProductVO> execute() {
        return productConvert.toVOList(productMapper.listProduct());
    }
}
