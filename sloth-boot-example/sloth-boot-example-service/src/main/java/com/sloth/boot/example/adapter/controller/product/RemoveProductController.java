package com.sloth.boot.example.adapter.controller.product;

import com.sloth.boot.common.log.annotation.OperateLog;
import com.sloth.boot.common.log.annotation.OperateTypeEnum;
import com.sloth.boot.common.result.R;
import com.sloth.boot.example.application.command.product.DeleteProductCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 删除商品接口。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Tag(name = "商品管理")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class RemoveProductController {

    private final DeleteProductCommand deleteProductCommand;

    @Operation(summary = "删除商品")
    @Parameter(name = "id", required = true, description = "商品ID", example = "1")
    @OperateLog(module = "商品管理", description = "删除商品", type = OperateTypeEnum.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable Long id) {
        deleteProductCommand.execute(id);
        return R.ok();
    }
}
