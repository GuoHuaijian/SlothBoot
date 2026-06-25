package com.sloth.boot.example.application.command.product;

import com.sloth.boot.common.exception.BizException;
import com.sloth.boot.example.application.model.enums.product.ProductErrorCode;
import com.sloth.boot.example.infrastructure.model.po.product.Product;
import com.sloth.boot.example.infrastructure.repository.mapper.product.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 删除商品命令。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteProductCommand {

    private final ProductMapper productMapper;

    /**
     * 执行删除商品操作。
     * <p>
     * 根据商品ID删除商品记录（物理删除）。
     *
     * @param id 商品ID
     * @throws BizException 当商品不存在时抛出
     */
    @Transactional(rollbackFor = Exception.class)
    public void execute(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) throw BizException.of(ProductErrorCode.PRODUCT_NOT_FOUND);
        productMapper.deleteById(id);
        log.info("删除商品成功: id={}", id);
    }
}
