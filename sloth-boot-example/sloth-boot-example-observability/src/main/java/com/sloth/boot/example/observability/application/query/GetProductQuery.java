package com.sloth.boot.example.observability.application.query;

import com.sloth.boot.common.exception.BizException;
import com.sloth.boot.example.observability.application.model.convert.ProductConvert;
import com.sloth.boot.example.observability.application.model.enums.product.ProductErrorCode;
import com.sloth.boot.example.observability.application.model.vo.ProductVO;
import com.sloth.boot.example.observability.infrastructure.model.po.product.DemoProduct;
import com.sloth.boot.example.observability.infrastructure.repository.mapper.DemoProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 商品详情查询（读操作）。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GetProductQuery {

    private final DemoProductMapper productMapper;
    private final ProductConvert productConvert;

    /**
     * 执行商品详情查询。
     *
     * @param id 商品 ID
     * @return 商品视图对象
     */
    public ProductVO execute(Long id) {
        log.info("Querying product: productId={}", id);
        DemoProduct product = productMapper.selectById(id);
        if (product == null) {
            throw BizException.of(ProductErrorCode.PRODUCT_NOT_FOUND, "商品不存在: " + id);
        }
        return productConvert.toVO(product);
    }
}
