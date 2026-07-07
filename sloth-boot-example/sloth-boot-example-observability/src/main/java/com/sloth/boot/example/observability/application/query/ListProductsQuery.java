package com.sloth.boot.example.observability.application.query;

import com.sloth.boot.example.observability.application.model.convert.ProductConvert;
import com.sloth.boot.example.observability.application.model.vo.ProductVO;
import com.sloth.boot.example.observability.infrastructure.repository.mapper.DemoProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 商品列表查询（读操作）。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ListProductsQuery {

    private final DemoProductMapper productMapper;
    private final ProductConvert productConvert;

    /**
     * 执行商品列表查询。
     *
     * @return 商品列表
     */
    public List<ProductVO> execute() {
        log.info("Listing products");
        return productConvert.toVOList(productMapper.selectAllOrdered());
    }
}
