package com.sloth.boot.example.adapter.controller.product;

import com.sloth.boot.common.log.annotation.OperateLog;
import com.sloth.boot.common.log.annotation.OperateTypeEnum;
import com.sloth.boot.common.result.R;
import com.sloth.boot.example.application.command.product.AddProductCommand;
import com.sloth.boot.example.application.model.form.product.ProductCreateForm;
import com.sloth.boot.example.application.model.vo.product.ProductVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 创建商品接口。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Tag(name = "商品管理")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class AddProductController {

    private final AddProductCommand addProductCommand;

    @Operation(summary = "创建商品")
    @OperateLog(module = "商品管理", description = "创建商品", type = OperateTypeEnum.CREATE)
    @PostMapping
    public R<ProductVO> add(@Valid @RequestBody ProductCreateForm form) {
        return R.ok(addProductCommand.execute(form));
    }
}
