package com.sloth.boot.example.application.command.product;

import com.sloth.boot.common.security.xss.XssCleaner;
import com.sloth.boot.example.application.model.convert.product.ProductConvert;
import com.sloth.boot.example.application.model.form.product.ProductCreateForm;
import com.sloth.boot.example.application.model.vo.product.ProductVO;
import com.sloth.boot.example.infrastructure.model.po.product.Product;
import com.sloth.boot.example.infrastructure.repository.mapper.product.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 创建商品命令。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AddProductCommand {

    private final ProductMapper productMapper;
    private final ProductConvert productConvert;

    /**
     * 执行创建商品操作。
     * <p>
     * 将商品表单数据转换为实体，进行XSS清洗后持久化到数据库。
     *
     * @param form 商品创建表单
     * @return 创建成功的商品视图对象
     */
    @Transactional(rollbackFor = Exception.class)
    public ProductVO execute(ProductCreateForm form) {
        Product product = productConvert.toEntity(form);
        product.setDescription(XssCleaner.cleanText(product.getDescription()));
        product.setStatus(0);
        productMapper.insert(product);
        log.info("创建商品成功: id={}, name={}", product.getId(), product.getName());
        return productConvert.toVO(product);
    }
}
