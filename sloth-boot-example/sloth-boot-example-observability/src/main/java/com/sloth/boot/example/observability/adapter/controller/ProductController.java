package com.sloth.boot.example.observability.adapter.controller;

import com.sloth.boot.common.result.R;
import com.sloth.boot.example.observability.application.model.vo.ProductVO;
import com.sloth.boot.example.observability.application.query.GetProductQuery;
import com.sloth.boot.example.observability.application.query.ListProductsQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品演示接口。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Tag(name = "商品演示", description = "商品列表与详情查询，作为级联调用链终端")
@RestController
@RequestMapping("/api/demo/products")
@RequiredArgsConstructor
public class ProductController {

    private final ListProductsQuery listProductsQuery;
    private final GetProductQuery getProductQuery;

    @Operation(summary = "商品列表", description = "查询全部商品")
    @GetMapping
    public R<List<ProductVO>> listProducts() {
        return R.ok(listProductsQuery.execute());
    }

    @Operation(summary = "商品详情", description = "查询商品详情")
    @Parameter(name = "id", description = "商品 ID", required = true, example = "1")
    @GetMapping("/{id}")
    public R<ProductVO> getProduct(@PathVariable Long id) {
        return R.ok(getProductQuery.execute(id));
    }
}
