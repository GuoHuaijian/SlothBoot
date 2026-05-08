package com.sloth.boot.example.controller;

import com.sloth.boot.common.annotation.OperateLog;
import com.sloth.boot.common.enums.OperateTypeEnum;
import com.sloth.boot.common.result.R;
import com.sloth.boot.example.dto.ProductCreateRequest;
import com.sloth.boot.example.dto.ProductDTO;
import com.sloth.boot.example.service.ProductDemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductDemoService productService;

    @GetMapping("/{id}")
    public R<ProductDTO> getProduct(@PathVariable Long id) {
        ProductDTO product = productService.getProduct(id);
        if (product == null) {
            return R.fail("商品不存在（布隆过滤器拦截或缓存未命中）");
        }
        return R.ok(product);
    }

    @OperateLog(module = "商品管理", description = "查询商品列表", type = OperateTypeEnum.QUERY)
    @GetMapping("/list")
    public R<List<ProductDTO>> listProducts() {
        return R.ok(productService.listProducts());
    }

    @OperateLog(module = "商品管理", description = "创建商品", type = OperateTypeEnum.CREATE)
    @PostMapping
    public R<ProductDTO> createProduct(@RequestBody ProductCreateRequest request) {
        return R.ok(productService.createProduct(request));
    }

    @OperateLog(module = "商品管理", description = "更新商品", type = OperateTypeEnum.UPDATE)
    @PutMapping("/{id}")
    public R<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductCreateRequest request) {
        return R.ok(productService.updateProduct(id, request));
    }

    @OperateLog(module = "商品管理", description = "删除商品", type = OperateTypeEnum.DELETE)
    @DeleteMapping("/{id}")
    public R<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return R.ok("删除成功");
    }

    @GetMapping("/rank")
    public R<?> getRank() {
        return R.ok(productService.getRank());
    }

    @PostMapping("/rank/vote")
    public R<String> voteProduct(@RequestParam Long productId) {
        productService.voteProduct(productId);
        return R.ok("投票成功");
    }

    @GetMapping("/cache/demo")
    public R<Map<String, Object>> demoCacheStrategies() {
        return R.ok(productService.demoCacheStrategies());
    }

    @GetMapping("/bloom/stats")
    public R<Map<String, Object>> getBloomStats() {
        return R.ok(productService.getBloomStats());
    }

    @PostMapping("/bloom/reset")
    public R<String> resetBloom() {
        productService.resetBloom();
        return R.ok("布隆过滤器已重置");
    }
}
