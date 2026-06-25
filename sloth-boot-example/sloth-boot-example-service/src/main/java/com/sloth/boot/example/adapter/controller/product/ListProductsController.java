package com.sloth.boot.example.adapter.controller.product;

import com.sloth.boot.common.result.R;
import com.sloth.boot.example.application.model.vo.product.ProductVO;
import com.sloth.boot.example.application.query.product.ListProductsQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品列表查询接口。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Tag(name = "商品管理")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ListProductsController {

    private final ListProductsQuery productListQuery;

    @Operation(summary = "商品列表")
    @GetMapping
    public R<List<ProductVO>> list() {
        return R.ok(productListQuery.execute());
    }
}
